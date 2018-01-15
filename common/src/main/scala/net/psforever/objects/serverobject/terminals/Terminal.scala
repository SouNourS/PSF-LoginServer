// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.terminals

import net.psforever.objects.Player
import net.psforever.objects.serverobject.PlanetSideServerObject
import net.psforever.packet.game.{ItemTransactionMessage, PlanetSideGUID}
import net.psforever.types.{TransactionType, Vector3}

/**
  * A structure-owned server object that is a "terminal" that can be accessed for amenities and services.
  * @param tdef the `ObjectDefinition` that constructs this object and maintains some of its immutable fields
  */
class Terminal(tdef : TerminalDefinition) extends PlanetSideServerObject {
  /** An entry that maintains a reference to the `Player`, and the player's GUID and location when the message was received. */
  private var hackedBy : Option[(Player, PlanetSideGUID, Vector3)] = None

  def HackedBy : Option[(Player, PlanetSideGUID, Vector3)] = hackedBy

  def HackedBy_=(agent : Player) : Option[(Player, PlanetSideGUID, Vector3)] = HackedBy_=(Some(agent))

  /**
    * Set the hack state of this object by recording important information about the player that caused it.
    * Set the hack state if there is no current hack state.
    * Override the hack state with a new hack state if the new user has different faction affiliation.
    * @param agent a `Player`, or no player
    * @return the player hack entry
    */
  def HackedBy_=(agent : Option[Player]) : Option[(Player, PlanetSideGUID, Vector3)] = {
    hackedBy match {
      case None =>
        //set the hack state if there is no current hack state
        if(agent.isDefined) {
          hackedBy = Some(agent.get, agent.get.GUID, agent.get.Position)
        }
      case Some(_) =>
        //clear the hack state
        if(agent.isEmpty) {
          hackedBy = None
        }
        //override the hack state with a new hack state if the new user has different faction affiliation
        else if(agent.get.Faction != hackedBy.get._1.Faction) {
          hackedBy = Some(agent.get, agent.get.GUID, agent.get.Position)
        }
    }
    HackedBy
  }

  //the following fields and related methods are neither finalized nor integrated; GOTO Request
  private var health : Int = 100 //TODO not real health value

  def Health : Int = health

  def Damaged(dam : Int) : Unit = {
    health = Math.max(0, Health - dam)
  }

  def Repair(rep : Int) : Unit = {
    health = Math.min(Health + rep, 100)
  }

  /**
    * Process some `TransactionType` action requested by the user.
    * @param player the player
    * @param msg the original packet carrying the request
    * @return an actionable message that explains what resulted from interacting with this `Terminal`
    */
  def Request(player : Player, msg : ItemTransactionMessage) : Terminal.Exchange = {
    msg.transaction_type match {
      case TransactionType.Buy | TransactionType.Learn =>
        tdef.Buy(player, msg)

      case TransactionType.Sell =>
        tdef.Sell(player, msg)

      case TransactionType.InfantryLoadout =>
        tdef.Loadout(player, msg)

      case _ =>
        Terminal.NoDeal()
    }
  }

  def Definition : TerminalDefinition = tdef
}

object Terminal {
  /**
    * Entry message into this `Terminal` that carries the request.
    * Accessing an option in a `Terminal` normally always results in this message.
    * @param player the player who sent this request message
    * @param msg the original packet carrying the request
    */
  final case class Request(player : Player, msg : ItemTransactionMessage)

  /**
    * A basic `Trait` connecting all of the actionable `Terminal` response messages.
    */
  sealed trait Exchange

  /**
    * Message that carries the result of the processed request message back to the original user (`player`).
    * @param player the player who sent this request message
    * @param msg the original packet carrying the request
    * @param response the result of the processed request
    */
  final case class TerminalMessage(player : Player, msg : ItemTransactionMessage, response : Exchange)

  /**
    * No action will result from interacting with this `Terminal`.
    * A result of a processed request.
    */
  final case class NoDeal() extends Exchange

  import net.psforever.types.ExoSuitType
  /**
    * The `Player` exo-suit will be changed to the prescribed one.
    * The subtype will be important if the user is swapping to an `ExoSuitType.MAX` exo-suit.
    * A result of a processed request.
    * @param exosuit the type of exo-suit
    * @param subtype the exo-suit subtype, if any
    */
  final case class BuyExosuit(exosuit : ExoSuitType.Value, subtype : Int = 0) extends Exchange

  import net.psforever.objects.equipment.Equipment
  /**
    * A single piece of `Equipment` has been selected and will be given to the `Player`.
    * The `Player` must decide what to do with it once it is in their control.
    * A result of a processed request.
    * @param item the `Equipment` being given to the player
    */
  final case class BuyEquipment(item : Equipment) extends Exchange

  /**
    * A roundabout message oft-times.
    * Most `Terminals` should always allow `Player`s to dispose of some piece of `Equipment`.
    * A result of a processed request.
    */
  final case class SellEquipment() extends Exchange //TODO if there are exceptions, find them

  import net.psforever.types.CertificationType
  /**
    * Provide the certification type unlocked by the player.
    * @param cert the certification unlocked
    * @param cost the certification point cost
    */
  final case class LearnCertification(cert : CertificationType.Value, cost : Int) extends Exchange

  /**
    * Provide the certification type freed-up by the player.
    * @param cert the certification returned
    * @param cost the certification point cost
    */
  final case class SellCertification(cert : CertificationType.Value, cost : Int) extends Exchange

  import net.psforever.objects.definition.ImplantDefinition
  /**
    * Provide the implant type unlocked by the player.
    * @param implant the implant (definition) requested
    */
  final case class LearnImplant(implant : ImplantDefinition) extends Exchange

  /**
    * Provide the implant type freed-up by the player.
    * @param implant the implant (definition) returned
    */
  final case class SellImplant(implant : ImplantDefinition) extends Exchange

  import net.psforever.objects.Vehicle
  import net.psforever.objects.inventory.InventoryItem
  /**
    * Provide a vehicle that was constructed for the player.
    * @param vehicle the vehicle
    * @param weapons the vehicle's mounted armament
    * @param inventory the vehicle's trunk contents
    */
  final case class BuyVehicle(vehicle : Vehicle, weapons : List[InventoryItem], inventory : List[InventoryItem]) extends Exchange

  /**
    * Recover a former exo-suit and `Equipment` configuration that the `Player` possessed.
    * A result of a processed request.
    * @param exosuit the type of exo-suit
    * @param subtype the exo-suit subtype, if any
    * @param holsters the contents of the `Player`'s holsters
    * @param inventory the contents of the `Player`'s inventory
    */
  final case class InfantryLoadout(exosuit : ExoSuitType.Value, subtype : Int = 0, holsters : List[InventoryItem], inventory : List[InventoryItem]) extends Exchange

  /**
    * Overloaded constructor.
    * @param tdef the `ObjectDefinition` that constructs this object and maintains some of its immutable fields
    */
  def apply(tdef : TerminalDefinition) : Terminal = {
    new Terminal(tdef)
  }

  import akka.actor.ActorContext
  /**
    * Instantiate an configure a `Terminal` object
    * @param tdef the `ObjectDefinition` that constructs this object and maintains some of its immutable fields
    * @param id the unique id that will be assigned to this entity
    * @param context a context to allow the object to properly set up `ActorSystem` functionality
    * @return the `Terminal` object
    */
  def Constructor(tdef : TerminalDefinition)(id : Int, context : ActorContext) : Terminal = {
    import akka.actor.Props

    val obj = Terminal(tdef)
    obj.Actor = context.actorOf(Props(classOf[TerminalControl], obj), s"${tdef.Name}_$id")
    obj
  }
}
