// Copyright (c) 2017 PSForever
import java.net.{InetAddress, InetSocketAddress}

import akka.actor.{Actor, ActorRef, Cancellable, MDCContextAware}
import net.psforever.packet.{PlanetSideGamePacket, _}
import net.psforever.packet.control._
import net.psforever.packet.game._
import org.log4s.MDC
import scodec.Attempt.{Failure, Successful}
import scodec.bits._
import MDCContextAware.Implicits._
import com.github.mauricio.async.db.{Connection, QueryResult, RowData}
import com.github.mauricio.async.db.mysql.MySQLConnection
import com.github.mauricio.async.db.mysql.exceptions.MySQLException
import com.github.mauricio.async.db.mysql.util.URLParser
import net.psforever.types.PlanetSideEmpire

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Random

class LoginSessionActor extends Actor with MDCContextAware {
  private[this] val log = org.log4s.getLogger

  import scala.concurrent.ExecutionContext.Implicits.global
  private case class UpdateServerList()

  var sessionId : Long = 0
  var leftRef : ActorRef = ActorRef.noSender
  var rightRef : ActorRef = ActorRef.noSender

  var updateServerListTask : Cancellable = null

  override def postStop() = {
    if(updateServerListTask != null)
      updateServerListTask.cancel()
  }

  def receive = Initializing

  def Initializing : Receive = {
    case HelloFriend(sessionId, right) =>
      this.sessionId = sessionId
      leftRef = sender()
      rightRef = right.asInstanceOf[ActorRef]

      context.become(Started)
    case _ =>
      log.error("Unknown message")
      context.stop(self)
  }

  def Started : Receive = {
    case UpdateServerList() =>
      updateServerList()
    case ctrl @ ControlPacket(_, _) =>
      handlePktContainer(ctrl)
    case game @ GamePacket(_, _, _) =>
      handlePktContainer(game)
    case default => failWithError(s"Invalid packet class received: $default")
  }

  def handlePkt(pkt : PlanetSidePacket) : Unit = pkt match {
    case ctrl : PlanetSideControlPacket =>
      handleControlPkt(ctrl)
    case game : PlanetSideGamePacket =>
      handleGamePkt(game)
    case default => failWithError(s"Invalid packet class received: $default")
  }

  def handlePktContainer(pkt : PlanetSidePacketContainer) : Unit = pkt match {
    case ctrl @ ControlPacket(opcode, ctrlPkt) =>
      handleControlPkt(ctrlPkt)
    case game @ GamePacket(opcode, seq, gamePkt) =>
      handleGamePkt(gamePkt)
    case default => failWithError(s"Invalid packet container class received: $default")
  }

  def handleControlPkt(pkt : PlanetSideControlPacket) = {
    pkt match {
      case SlottedMetaPacket(slot, subslot, innerPacket) =>
        // Meta packets are like TCP packets - then need to be ACKed to the client
        sendResponse(PacketCoding.CreateControlPacket(SlottedMetaAck(slot, subslot)))

        // Decode the inner packet and handle it or error
        PacketCoding.DecodePacket(innerPacket).fold({
          error => log.error(s"Failed to decode inner packet of SlottedMetaPacket: $error")
        }, {
          handlePkt(_)
        })
      /// TODO: figure out what this is what what it does for the PS client
      /// I believe it has something to do with reliable packet transmission and resending
      case sync @ ControlSync(diff, unk, f1, f2, f3, f4, fa, fb) =>
        log.trace(s"SYNC: ${sync}")

        val serverTick = Math.abs(System.nanoTime().toInt) // limit the size to prevent encoding error
        sendResponse(PacketCoding.CreateControlPacket(ControlSyncResp(diff, serverTick,
          fa, fb, fb, fa)))
      case MultiPacket(packets) =>

        /// Extract out each of the subpackets in the MultiPacket and handle them or raise a packet error
        packets.foreach { pkt =>
          PacketCoding.DecodePacket(pkt).fold({ error =>
            log.error(s"Failed to decode inner packet of MultiPacket: $error")
          }, {
            handlePkt(_)
          })
        }
      case default =>
        log.error(s"Unhandled ControlPacket $default")
    }
  }

  // TODO: move to global configuration or database lookup
  val serverName = "PSForever"
  val serverAddress = new InetSocketAddress(LoginConfig.serverIpAddress.getHostAddress, 51001)

  // TESTING CODE FOR ACCOUNT LOOKUP
  def accountLookup(username : String, password : String) : Boolean = {
    val connection: Connection = DatabaseConnector.getAccountsConnection

    Await.result(connection.connect, 5 seconds)

    // create account
    //   username, password, email
    //   Result: worked or failed
    // login to account
    //   username, password
    //   Result: token (session cookie)

    val future: Future[QueryResult] = connection.sendPreparedStatement("SELECT * FROM accounts where username=?", Array(username))

    val mapResult: Future[Any] = future.map(queryResult => queryResult.rows match {
      case Some(resultSet) => {
        val row : RowData = resultSet.head
        row(0)
      }
      case None => -1
    }
    )

    try {
      // XXX: remove awaits
      val result = Await.result( mapResult, 5 seconds )
      return true
    } catch {
      case e : MySQLException =>
        log.error(s"SQL exception $e")
      case e: Exception =>
        log.error(s"Unknown exception when executing SQL statement: $e")
    } finally {
      connection.disconnect
    }

    false
  }

  def handleGamePkt(pkt : PlanetSideGamePacket) = pkt match {
      case LoginMessage(majorVersion, minorVersion, buildDate, username,
        password, token, revision) =>
        // TODO: prevent multiple LoginMessages from being processed in a row!! We need a state machine
        import game.LoginRespMessage._

        val clientVersion = s"Client Version: ${majorVersion}.${minorVersion}.${revision}, ${buildDate}"

        if(token.isDefined)
          log.info(s"New login UN:$username Token:${token.get}. ${clientVersion}")
        else
          log.info(s"New login UN:$username PW:$password. ${clientVersion}")

        // This is temporary until a schema has been developed
        //val loginSucceeded = accountLookup(username, password.getOrElse(token.get))

        // Allow any one to login for now
        val loginSucceeded = true

        if(loginSucceeded) {
          val newToken = token.getOrElse("AAAABBBBCCCCDDDDEEEEFFFFGGGGHHH")
          val response = LoginRespMessage(newToken, LoginError.Success, StationError.AccountActive,
            StationSubscriptionStatus.Active, 0, username, 10001)

          sendResponse(PacketCoding.CreateGamePacket(0, response))

          updateServerListTask = context.system.scheduler.schedule(0 seconds, 2 seconds, self, UpdateServerList())
        } else {
          val newToken = token.getOrElse("AAAABBBBCCCCDDDDEEEEFFFFGGGGHHH")
          val response = LoginRespMessage(newToken, LoginError.BadUsernameOrPassword, StationError.AccountActive,
            StationSubscriptionStatus.Active, 685276011, username, 10001)

          log.info(s"Failed login to account ${username}")
          sendResponse(PacketCoding.CreateGamePacket(0, response))
        }
      case ConnectToWorldRequestMessage(name, _, _, _, _, _, _) =>
        log.info(s"Connect to world request for '${name}'")

        val response = ConnectToWorldMessage(serverName, serverAddress.getHostString, serverAddress.getPort)
        sendResponse(PacketCoding.CreateGamePacket(0, response))
        sendResponse(DropSession(sessionId, "user transferring to world"))
      case default => log.debug(s"Unhandled GamePacket ${pkt}")
  }

  def updateServerList() = {
    val msg = VNLWorldStatusMessage("Welcome to PlanetSide! ",
      Vector(
        WorldInformation(serverName, WorldStatus.Up, ServerType.Beta,
          Vector(WorldConnectionInfo(serverAddress)), PlanetSideEmpire.VS)
      ))

    sendResponse(PacketCoding.CreateGamePacket(0, msg))
  }

  def failWithError(error : String) = {
    log.error(error)
    //sendResponse(PacketCoding.CreateControlPacket(ConnectionClose()))
  }

  def sendResponse(cont : Any) = {
    log.trace("LOGIN SEND: " + cont)

    MDC("sessionId") = sessionId.toString
    rightRef !> cont
  }

  def sendRawResponse(pkt : ByteVector) = {
    log.trace("LOGIN SEND RAW: " + pkt)

    MDC("sessionId") = sessionId.toString
    rightRef !> RawPacket(pkt)
  }
}
