// Copyright (c) 2017 PSForever
import java.net.InetAddress
import java.io.File
import java.util.Locale

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import akka.routing.RandomPool
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.status._
import ch.qos.logback.core.util.StatusPrinter
import com.typesafe.config.ConfigFactory
import net.psforever.crypto.CryptoInterface
import net.psforever.objects.zones._
import net.psforever.objects.guid.TaskResolver
import net.psforever.objects.serverobject.builders._
import net.psforever.types.Vector3
import org.slf4j
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._
import services.ServiceManager
import services.avatar._
import services.chat.ChatService
import services.local._
import services.vehicle.VehicleService

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

object PsLogin {
  private val logger = org.log4s.getLogger

  var args : Array[String] = Array()
  var config : java.util.Map[String,Object] = null
  implicit var system : ActorSystem = null
  var loginRouter : Props = Props.empty
  var worldRouter : Props = Props.empty
  var loginListener : ActorRef = ActorRef.noSender
  var worldListener : ActorRef = ActorRef.noSender

  def banner() : Unit = {
    println(ansi().fgBright(BLUE).a("""   ___  ________"""))
    println(ansi().fgBright(BLUE).a("""  / _ \/ __/ __/__  _______ _  _____ ____"""))
    println(ansi().fgBright(MAGENTA).a(""" / ___/\ \/ _// _ \/ __/ -_) |/ / -_) __/"""))
    println(ansi().fgBright(RED).a("""/_/  /___/_/  \___/_/  \__/|___/\__/_/""").reset())
    println("""   Login Server - PSForever Project""")
    println("""        http://psforever.net""")
    println
  }

  /** Grabs the most essential system information and returns it as a preformatted string */
  def systemInformation : String = {
    s"""|~~~ System Information ~~~
       |${System.getProperty("os.name")} (v. ${System.getProperty("os.version")}, ${System.getProperty("os.arch")})
       |${System.getProperty("java.vm.name")} (build ${System.getProperty("java.version")}), ${System.getProperty("java.vendor")} - ${System.getProperty("java.vendor.url")}
    """.stripMargin
  }

  /** Used to enumerate all of the Java properties. Used in testing only */
  def enumerateAllProperties() : Unit = {
    val props = System.getProperties
    val enums = props.propertyNames()

    while(enums.hasMoreElements) {
      val key = enums.nextElement.toString
      System.out.println(key + " : " + props.getProperty(key))
    }
  }

  /**
    * Checks the current logger context
    * @param context SLF4J logger context
    * @return Boolean return true if context has errors
    */
  def loggerHasErrors(context : LoggerContext) = {
    val statusUtil = new StatusUtil(context)

    statusUtil.getHighestLevel(0) >= Status.WARN
  }

  /** Loads the logging configuration and starts logging */
  def initializeLogging(logfile : String) : Unit = {
    // assume SLF4J is bound to logback in the current environment
    val lc = slf4j.LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

    try {
      val configurator = new JoranConfigurator()
      configurator.setContext(lc)

      // reset any loaded settings
      lc.reset()
      configurator.doConfigure(logfile)
    }
    catch {
      case _ : JoranException => ;
    }

    if(loggerHasErrors(lc)) {
      println("Loading log settings failed")
      StatusPrinter.printInCaseOfErrorsOrWarnings(lc)
      sys.exit(1)
    }
  }

  def parseArgs(args : Array[String]) : Unit = {
    if(args.length == 1) {
      LoginConfig.serverIpAddress = InetAddress.getByName(args{0})
    }
    else {
      LoginConfig.serverIpAddress = InetAddress.getLocalHost
    }
  }

  def run() : Unit = {
    // Early start up
    banner()
    println(systemInformation)

    // Config directory
    // Assume a default of the current directory
    var configDirectory = "config"

    // This is defined when we are running from SBT pack
    if(System.getProperty("prog.home") != null) {
      configDirectory = System.getProperty("prog.home") + File.separator + "config"
    }

    initializeLogging(configDirectory + File.separator + "logback.xml")
    parseArgs(this.args)

    /** Initialize the PSCrypto native library
      *
      * PSCrypto provides PlanetSide specific crypto that is required to communicate with it.
      * It has to be distributed as a native library because there is no Scala version of the required
      * cryptographic primitives (MD5MAC). See https://github.com/psforever/PSCrypto for more information.
      */
    try {
      CryptoInterface.initialize()
      logger.info("PSCrypto initialized")
    }
    catch {
      case e : UnsatisfiedLinkError =>
        logger.error("Unable to initialize " + CryptoInterface.libName)
        logger.error(e)("This means that your PSCrypto version is out of date. Get the latest version from the README" +
          " https://github.com/psforever/PSF-LoginServer#downloading-pscrypto")
        sys.exit(1)
      case e : IllegalArgumentException =>
        logger.error("Unable to initialize " + CryptoInterface.libName)
        logger.error(e)("This means that your PSCrypto version is out of date. Get the latest version from the README" +
          " https://github.com/psforever/PSF-LoginServer#downloading-pscrypto")
        sys.exit(1)
    }

    // TODO: pluralize "processors"
    logger.info(s"Detected ${Runtime.getRuntime.availableProcessors()} available logical processors")
    logger.info("Starting actor subsystems...")

    /** Make sure we capture Akka messages (but only INFO and above)
      *
      * This same config can be specified in a configuration file, but that's more work at this point.
      * In the future we will have a unified configuration file specific to this server
      */
    config = Map(
      "akka.loggers" -> List("akka.event.slf4j.Slf4jLogger").asJava,
      "akka.loglevel" -> "INFO",
      "akka.logging-filter" -> "akka.event.slf4j.Slf4jLoggingFilter"
    ).asJava

    /** Start up the main actor system. This "system" is the home for all actors running on this server */
    system = ActorSystem("PsLogin", ConfigFactory.parseMap(config))

    /** Create pipelines for the login and world servers
      *
      * The first node in the pipe is an Actor that handles the crypto for protecting packets.
      * After any crypto operations have been applied or unapplied, the packets are passed on to the next
      * actor in the chain. For an incoming packet, this is a player session handler. For an outgoing packet
      * this is the session router, which returns the packet to the sending host.
      *
      * See SessionRouter.scala for a diagram
      */
    val loginTemplate = List(
      SessionPipeline("crypto-session-", Props[CryptoSessionActor]),
      SessionPipeline("packet-session-", Props[PacketCodingActor]),
      SessionPipeline("login-session-", Props[LoginSessionActor])
    )
    val worldTemplate = List(
      SessionPipeline("crypto-session-", Props[CryptoSessionActor]),
      SessionPipeline("packet-session-", Props[PacketCodingActor]),
      SessionPipeline("world-session-", Props[WorldSessionActor])
    )

    val loginServerPort = 51000
    val worldServerPort = 51001


    // Uncomment for network simulation
    // TODO: make this config or command flag
    /*
    val netParams = NetworkSimulatorParameters(
      packetLoss = 0.02,
      packetDelay = 500,
      packetReorderingChance = 0.005,
      packetReorderingTime = 400
    )
    */

    val serviceManager = ServiceManager.boot
    serviceManager ! ServiceManager.Register(RandomPool(50).props(Props[TaskResolver]), "taskResolver")
    serviceManager ! ServiceManager.Register(Props[AvatarService], "avatar")
    serviceManager ! ServiceManager.Register(Props[LocalService], "local")
    serviceManager ! ServiceManager.Register(Props[VehicleService], "vehicle")
    serviceManager ! ServiceManager.Register(Props[ChatService], "chat")
    serviceManager ! ServiceManager.Register(Props(classOf[InterstellarCluster], createContinents()), "galaxy")

    /** Create two actors for handling the login and world server endpoints */
    loginRouter = Props(new SessionRouter("Login", loginTemplate))
    worldRouter = Props(new SessionRouter("World", worldTemplate))
    loginListener = system.actorOf(Props(new UdpListener(loginRouter, "login-session-router", LoginConfig.serverIpAddress, loginServerPort, None)), "login-udp-endpoint")
    worldListener = system.actorOf(Props(new UdpListener(worldRouter, "world-session-router", LoginConfig.serverIpAddress, worldServerPort, None)), "world-udp-endpoint")

    logger.info(s"NOTE: Set client.ini to point to ${LoginConfig.serverIpAddress.getHostAddress}:$loginServerPort")

    // Add our shutdown hook (this works for Control+C as well, but not in Cygwin)
    sys addShutdownHook {
      // TODO: clean up active sessions and close resources safely
      logger.info("Login server now shutting down...")
    }
  }

  def createContinents() : List[Zone] = {
    val map13 = new ZoneMap("map13") {
      import net.psforever.objects.GlobalDefinitions._

      LocalObject(DoorObjectBuilder(door, 330))
      LocalObject(DoorObjectBuilder(door, 332))
      LocalObject(DoorObjectBuilder(door, 362))
      LocalObject(DoorObjectBuilder(door, 370))
      LocalObject(DoorObjectBuilder(door, 371))
      LocalObject(DoorObjectBuilder(door, 372))
      LocalObject(DoorObjectBuilder(door, 373))
      LocalObject(DoorObjectBuilder(door, 374))
      LocalObject(DoorObjectBuilder(door, 375))
      LocalObject(DoorObjectBuilder(door, 394))
      LocalObject(DoorObjectBuilder(door, 395))
      LocalObject(DoorObjectBuilder(door, 396))
      LocalObject(DoorObjectBuilder(door, 397))
      LocalObject(DoorObjectBuilder(door, 398))
      LocalObject(DoorObjectBuilder(door, 462))
      LocalObject(DoorObjectBuilder(door, 463))
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 520)) //Hart B
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 522)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 523)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 524)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 525)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 526)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 527)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 528)) //Hart C
      LocalObject(ImplantTerminalMechObjectBuilder(implant_terminal_mech, 529)) //Hart C
      LocalObject(IFFLockObjectBuilder(lock_external, 556))
      LocalObject(IFFLockObjectBuilder(lock_external, 558))
      LocalObject(TerminalObjectBuilder(cert_terminal, 186))
      LocalObject(TerminalObjectBuilder(cert_terminal, 187))
      LocalObject(TerminalObjectBuilder(cert_terminal, 188))
      LocalObject(TerminalObjectBuilder(order_terminal, 853))
      LocalObject(TerminalObjectBuilder(order_terminal, 855))
      LocalObject(TerminalObjectBuilder(order_terminal, 860))
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1081)) //tube 520
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1082)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1083)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1084)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1085)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1086)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1087)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1088)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(implant_terminal_interface, 1089)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(ground_vehicle_terminal, 1063))
      LocalObject(VehicleSpawnPadObjectBuilder(spawn_pad, 500)) //TODO guid not correct
      LocalObject(TerminalObjectBuilder(dropship_vehicle_terminal, 304))
      LocalObject(VehicleSpawnPadObjectBuilder(spawn_pad, 501)) //TODO guid not correct

      LocalBases = 30

      ObjectToBase(330, 29)
      ObjectToBase(332, 29)
      //ObjectToBase(520, 29)
      ObjectToBase(522, 29)
      ObjectToBase(523, 29)
      ObjectToBase(524, 29)
      ObjectToBase(525, 29)
      ObjectToBase(526, 29)
      ObjectToBase(527, 29)
      ObjectToBase(528, 29)
      ObjectToBase(529, 29)
      ObjectToBase(556, 29)
      ObjectToBase(558, 29)
      ObjectToBase(1081, 29)
      ObjectToBase(1063, 2) //TODO unowned courtyard terminal?
      ObjectToBase(500, 2) //TODO unowned courtyard spawnpad?
      ObjectToBase(304, 2) //TODO unowned courtyard terminal?
      ObjectToBase(501, 2) //TODO unowned courtyard spawnpad?

      DoorToLock(330, 558)
      DoorToLock(332, 556)
      TerminalToSpawnPad(1063, 500)
      TerminalToSpawnPad(304, 501)
      TerminalToInterface(520, 1081)
      TerminalToInterface(522, 1082)
      TerminalToInterface(523, 1083)
      TerminalToInterface(524, 1084)
      TerminalToInterface(525, 1085)
      TerminalToInterface(526, 1086)
      TerminalToInterface(527, 1087)
      TerminalToInterface(528, 1088)
      TerminalToInterface(529, 1089)
    }
    val home3 = new Zone("home3", map13, 13) {
      override def Init(implicit context : ActorContext) : Unit = {
        super.Init(context)

        import net.psforever.types.PlanetSideEmpire
        Base(2).get.Faction = PlanetSideEmpire.VS //HART building C
        Base(29).get.Faction = PlanetSideEmpire.NC //South Villa Gun Tower

        GUID(500) match {
          case Some(pad) =>
            pad.Position = Vector3(3506.0f, 2820.0f, 92.0f)
            pad.Orientation = Vector3(0f, 0f, 270.0f)
          case None => ;
        }
        GUID(501) match {
          case Some(pad) =>
            pad.Position = Vector3(3508.9844f, 2895.961f, 92.296875f)
            pad.Orientation = Vector3(0f, 0f, 270.0f)
          case None => ;
        }
      }
    }


    val map98 = new ZoneMap("map98") {

    }
    val i2 = Zone("i2", map98, 31)

    home3 :: i2 ::
      Nil
  }

  def main(args : Array[String]) : Unit = {
    Locale.setDefault(Locale.US); // to have floats with dots, not comma...
    this.args = args
    run()

    // Wait forever until the actor system shuts down
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
