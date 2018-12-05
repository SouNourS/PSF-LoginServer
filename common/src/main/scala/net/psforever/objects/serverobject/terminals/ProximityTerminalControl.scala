// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.terminals

import akka.actor.{Actor, ActorRef, Cancellable}
import net.psforever.objects.{DefaultCancellable, PlanetSideGameObject, Player, Vehicle}
import net.psforever.objects.serverobject.CommonMessages
import net.psforever.objects.serverobject.affinity.{FactionAffinity, FactionAffinityBehavior}
import net.psforever.packet.game.PlanetSideGUID
import services.local.{LocalAction, LocalServiceMessage}
import services.{Service, ServiceManager}

import scala.collection.mutable
import scala.concurrent.duration._

/**
  * An `Actor` that handles messages being dispatched to a specific `ProximityTerminal`.
  * Although this "terminal" itself does not accept the same messages as a normal `Terminal` object,
  * it returns the same type of messages - wrapped in a `TerminalMessage` - to the `sender`.
  * @param term the proximity unit (terminal)
  */
class ProximityTerminalControl(term : Terminal with ProximityUnit) extends Actor with FactionAffinityBehavior.Check {
  var service : ActorRef = ActorRef.noSender
  var terminalAction : Cancellable = DefaultCancellable.obj
  val callbacks : mutable.ListBuffer[(ActorRef, ActorRef)] = new mutable.ListBuffer[(ActorRef, ActorRef)]()
  val log = org.log4s.getLogger

  def FactionObject : FactionAffinity = term

  def TerminalObject : Terminal with ProximityUnit = term

  def receive : Receive = Start

  def Start : Receive = checkBehavior
    .orElse {
    case Service.Startup() =>
      ServiceManager.serviceManager ! ServiceManager.Lookup("local")

    case ServiceManager.LookupResult("local", ref) =>
      service = ref
      context.become(Run)

    case _ => ;
  }

  def Run : Receive = checkBehavior
    .orElse {
      case CommonMessages.Use(_, Some(target : PlanetSideGameObject)) =>
        if(term.Definition.asInstanceOf[ProximityDefinition].Validations.exists(p => p(target))) {
          Use(target, term.Continent, sender, sender)
        }

      case CommonMessages.Use(_, Some((target : PlanetSideGameObject, callback : ActorRef))) =>
        if(term.Definition.asInstanceOf[ProximityDefinition].Validations.exists(p => p(target))) {
          Use(target, term.Continent, callback, sender)
        }

      case CommonMessages.Use(_, _) =>
        log.warn(s"unexpected format for CommonMessages.Use in this context")

      case CommonMessages.Unuse(_, Some(target : PlanetSideGameObject)) =>
        Unuse(target, term.Continent)

      case CommonMessages.Unuse(_, _) =>
        log.warn(s"unexpected format for CommonMessages.Unuse in this context")

      case ProximityTerminalControl.TerminalAction() =>
        val proxDef = term.Definition.asInstanceOf[ProximityDefinition]
        val validateFunc : PlanetSideGameObject=>Boolean = term.Validate(proxDef.UseRadius * proxDef.UseRadius, proxDef.Validations)
        val callbackList = callbacks.toList
        term.Targets.zipWithIndex.foreach({ case((target, index)) =>
          if(validateFunc(target)) {
            callbackList.lift(index) match {
              case Some((_, cback)) =>
                cback ! ProximityUnit.Action(term, target)
              case None =>
                log.error(s"improper callback registered for $target on $term in zone ${term.Owner.Continent}; this may be recoverable")
            }
          }
          else {
            Unuse(target, term.Continent)
          }
        })

      case CommonMessages.Hack(player) =>
        term.HackedBy = player
        sender ! true

      case CommonMessages.ClearHack() =>
        term.HackedBy = None

      case ProximityUnit.Action(_, _) =>
        //reserved

      case _ => ;
    }

  def Use(target : PlanetSideGameObject, zone : String, callback : ActorRef, sender : ActorRef) : Unit = {
    val hadNoUsers = term.NumberUsers == 0
    if(term.AddUser(target)) {
      //add callback
      callbacks += ((sender, callback))
      //activation
      if(term.NumberUsers == 1 && hadNoUsers) {
        val medDef = term.Definition.asInstanceOf[MedicalTerminalDefinition]
        import scala.concurrent.ExecutionContext.Implicits.global
        terminalAction.cancel
        terminalAction = context.system.scheduler.schedule(500 milliseconds, medDef.Interval, self, ProximityTerminalControl.TerminalAction())
        service ! LocalServiceMessage(zone, LocalAction.ProximityTerminalEffect(PlanetSideGUID(0), term.GUID, true))
      }
    }
    else {
      log.warn(s"ProximityTerminal.Use: $target was rejected by unit ${term.Definition.Name}@${term.GUID.guid}")
    }
  }

  def Unuse(target : PlanetSideGameObject, zone : String) : Unit = {
    val whereTarget = term.Targets.indexWhere(_ eq target)
    val previousUsers = term.NumberUsers
    val hadUsers = previousUsers > 0
    if(whereTarget > -1 && term.RemoveUser(target)) {
      //remove callback
      val originalSender : ActorRef = {
        val (to, _) = callbacks.remove(whereTarget)
        to
      }
      //de-activation (global / local)
      if(term.NumberUsers == 0 && hadUsers) {
        terminalAction.cancel
        service ! LocalServiceMessage(zone, LocalAction.ProximityTerminalEffect(PlanetSideGUID(0), term.GUID, false))
      }
      else if(originalSender ne ActorRef.noSender) {
        originalSender ! ProximityUnit.CancelEffectUser(term.GUID)
      }
    }
    else {
      log.debug(s"target by proximity $target is not known to $term, though the unit tried to 'Unuse' it")
    }
  }

  override def toString : String = term.Definition.Name
}

object ProximityTerminalControl {
  object Validation {
    def Medical(target : PlanetSideGameObject) : Boolean = target match {
      case p : Player =>
        p.Health > 0 && (p.Health < p.MaxHealth || p.Armor < p.MaxArmor)
      case _ =>
        false
    }

    def HealthCrystal(target : PlanetSideGameObject) : Boolean = target match {
      case p : Player =>
        p.Health > 0 && p.Health < p.MaxHealth
      case _ =>
        false
    }

    def RepairSilo(target : PlanetSideGameObject) : Boolean = target match {
      case v : Vehicle =>
        v.Health > 0 && v.Health < v.MaxHealth
      case _ =>
        false
    }
  }

  private case class TerminalAction()

  private case class CancelTerminalAction()
}
