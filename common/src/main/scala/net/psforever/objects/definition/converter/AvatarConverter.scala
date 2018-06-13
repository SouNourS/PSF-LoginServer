// Copyright (c) 2017 PSForever
package net.psforever.objects.definition.converter

import net.psforever.objects.{EquipmentSlot, Player}
import net.psforever.objects.equipment.Equipment
import net.psforever.packet.game.objectcreate._
import net.psforever.types.{GrenadeState, ImplantType}

import scala.annotation.tailrec
import scala.util.{Success, Try}

class AvatarConverter extends ObjectCreateConverter[Player]() {
  override def ConstructorData(obj : Player) : Try[PlayerData] = {
    import AvatarConverter._
    Success(
      if(obj.VehicleSeated.isEmpty) {
        PlayerData(
          PlacementData(obj.Position, obj.Orientation, obj.Velocity),
          MakeAppearanceData(obj),
          MakeCharacterData(obj),
          MakeInventoryData(obj),
          GetDrawnSlot(obj)
        )
      }
      else {
        PlayerData(
          MakeAppearanceData(obj),
          MakeCharacterData(obj),
          MakeInventoryData(obj),
          DrawnSlot.None
        )
      }
    )
  }

  override def DetailedConstructorData(obj : Player) : Try[DetailedPlayerData] = {
    import AvatarConverter._
    Success(
      if(obj.VehicleSeated.isEmpty) {
        DetailedPlayerData.apply(
          PlacementData(obj.Position, obj.Orientation, obj.Velocity),
          MakeAppearanceData(obj),
          MakeDetailedCharacterData(obj),
          MakeDetailedInventoryData(obj),
          GetDrawnSlot(obj)
        )
      }
      else {
        DetailedPlayerData.apply(
          MakeAppearanceData(obj),
          MakeDetailedCharacterData(obj),
          MakeDetailedInventoryData(obj),
          DrawnSlot.None
        )
      }
    )
  }
}

object AvatarConverter {
  /**
    * Compose some data from a `Player` into a representation common to both `CharacterData` and `DetailedCharacterData`.
    * @param obj the `Player` game object
    * @return the resulting `CharacterAppearanceData`
    */
  def MakeAppearanceData(obj : Player) : (Int)=>CharacterAppearanceData = {
    CharacterAppearanceData(
      BasicCharacterData(obj.Name, obj.Faction, obj.Sex, obj.Head, obj.Voice),
      voice2 = 0,
      black_ops = false,
      jammered = false,
      obj.ExoSuit,
      outfit_name = "",
      outfit_logo = 0,
      obj.isBackpack,
      facingPitch = obj.Orientation.y,
      facingYawUpper = obj.FacingYawUpper,
      lfs = true,
      GrenadeState.None,
      is_cloaking = false,
      charging_pose = false,
      on_zipline = false,
      RibbonBars()
    )
  }

  def MakeCharacterData(obj : Player) : (Boolean,Boolean)=>CharacterData = {
    val MaxArmor = obj.MaxArmor
    CharacterData(
      255 * obj.Health / obj.MaxHealth, //TODO not precise
      if(MaxArmor == 0) {
        0
      }
      else {
        255 * obj.Armor / MaxArmor
      }, //TODO not precise
      DressBattleRank(obj),
      DressCommandRank(obj),
      recursiveMakeImplantEffects(obj.Implants.iterator),
      MakeCosmetics(obj.BEP)
    )
  }

  def MakeDetailedCharacterData(obj : Player) : (Option[Int])=>DetailedCharacterData = {
    DetailedCharacterData(
      obj.BEP,
      obj.CEP,
      obj.MaxHealth,
      obj.Health,
      obj.Armor,
      obj.MaxStamina,
      obj.Stamina,
      obj.Certifications.toList.sortBy(_.id), //TODO is sorting necessary?
      MakeImplantEntries(obj),
      firstTimeEvents = List.empty[String], //TODO fte list
      tutorials = List.empty[String], //TODO tutorial list
      MakeCosmetics(obj.BEP)
    )
  }

  def MakeInventoryData(obj : Player) : InventoryData = {
    InventoryData(MakeHolsters(obj, BuildEquipment).sortBy(_.parentSlot))
  }

  def MakeDetailedInventoryData(obj : Player) : InventoryData = {
    InventoryData((MakeHolsters(obj, BuildDetailedEquipment) ++ MakeFifthSlot(obj) ++ MakeInventory(obj)).sortBy(_.parentSlot))
  }

  /**
    * Select the appropriate `UniformStyle` design for a player's accumulated battle experience points.
    * At certain battle ranks, all exo-suits undergo some form of coloration change.
    * @param obj the `Player` game object
    * @return the resulting uniform upgrade level
    */
  private def DressBattleRank(obj : Player) : UniformStyle.Value = {
    val bep : Long = obj.BEP
    if(bep > 2583440) { //BR25+
      UniformStyle.ThirdUpgrade
    }
    else if(bep > 308989) { //BR14+
      UniformStyle.SecondUpgrade
    }
    else if(bep > 44999) { //BR7+
      UniformStyle.FirstUpgrade
    }
    else { //BR1+
      UniformStyle.Normal
    }
  }

  /**
    * Select the appropriate design for a player's accumulated command experience points.
    * Visual cues for command rank include armlets, anklets, and, finally, a backpack, awarded at different ranks.
    * @param obj the `Player` game object
    * @return the resulting uniform upgrade level
    */
  private def DressCommandRank(obj : Player) : Int = {
    val cep = obj.CEP
    if(cep > 599999) {
      5
    }
    else if(cep > 299999) {
      4
    }
    else if(cep > 149999) {
      3
    }
    else if(cep > 49999) {
      2
    }
    else if(cep > 9999) {
      1
    }
    else {
      0
    }
  }

  /**
    * Transform an `Array` of `Implant` objects into a `List` of `ImplantEntry` objects suitable as packet data.
    * @param obj the `Player` game object
    * @return the resulting implant `List`
    * @see `ImplantEntry` in `DetailedCharacterData`
    */
  private def MakeImplantEntries(obj : Player) : List[ImplantEntry] = {
    val numImplants : Int = DetailedCharacterData.numberOfImplantSlots(obj.BEP)
    val implants = obj.Implants
    obj.Implants.map({ case(implant, initialization, active) =>
      if(initialization == 0) {
        ImplantEntry(implant, None)
      }
      else {
        ImplantEntry(implant, Some(math.max(0,initialization).toInt))
      }
    }).toList
  }

  /**
    * Find an active implant whose effect will be displayed on this player.
    * @param iter an `Iterator` of `ImplantSlot` objects
    * @return the effect of an active implant
    */
  @tailrec private def recursiveMakeImplantEffects(iter : Iterator[(ImplantType.Value, Long, Boolean)]) : Option[ImplantEffects.Value] = {
    if(!iter.hasNext) {
      None
    }
    else {
      val(implant, _, active) = iter.next
      if(active) {
        implant match {
          case ImplantType.AdvancedRegen =>
            Some(ImplantEffects.RegenEffects)
          case ImplantType.DarklightVision =>
            Some(ImplantEffects.DarklightEffects)
          case ImplantType.PersonalShield =>
            Some(ImplantEffects.PersonalShieldEffects)
          case ImplantType.Surge =>
            Some(ImplantEffects.SurgeEffects)
          case _ =>
            recursiveMakeImplantEffects(iter)
        }
      }
      else {
        recursiveMakeImplantEffects(iter)
      }
    }
  }

  /**
    * Should this player be of battle rank 24 or higher, they will have a mandatory cosmetics object.
    * @param bep battle experience points
    * @see `Cosmetics`
    * @return the `Cosmetics` options
    */
  def MakeCosmetics(bep : Long) : Option[Cosmetics] =
    if(DetailedCharacterData.isBR24(bep)) {
      Some(Cosmetics(false, false, false, false, false))
    }
    else {
      None
    }

  /**
    * Given a player with an inventory, convert the contents of that inventory into converted-decoded packet data.
    * The inventory is not represented in a `0x17` `Player`, so the conversion is only valid for `0x18` avatars.
    * It will always be "`Detailed`".
    * @param obj the `Player` game object
    * @return a list of all items that were in the inventory in decoded packet form
    */
  private def MakeInventory(obj : Player) : List[InternalSlot] = {
    obj.Inventory.Items
      .map(item => {
          val equip : Equipment = item.obj
          InternalSlot(equip.Definition.ObjectId, equip.GUID, item.start, equip.Definition.Packet.DetailedConstructorData(equip).get)
      })
  }

  /**
    * Given a player with equipment holsters, convert the contents of those holsters into converted-decoded packet data.
    * The decoded packet form is determined by the function in the parameters as both `0x17` and `0x18` conversions are available,
    * with exception to the contents of the fifth slot.
    * The fifth slot is only represented if the `Player` is an `0x18` type.
    * @param obj the `Player` game object
    * @param builder the function used to transform to the decoded packet form
    * @return a list of all items that were in the holsters in decoded packet form
    */
  private def MakeHolsters(obj : Player, builder : ((Int, Equipment) => InternalSlot)) : List[InternalSlot] = {
    recursiveMakeHolsters(obj.Holsters().iterator, builder)
  }

  /**
    * Given a player with equipment holsters, convert any content of the fifth holster slot into converted-decoded packet data.
    * The fifth holster is a curious divider between the standard holsters and the formal inventory.
    * This fifth slot is only ever represented if the `Player` is an `0x18` type.
    * @param obj the `Player` game object
    * @return a list of any item that was in the fifth holster in decoded packet form
    */
  private def MakeFifthSlot(obj : Player) : List[InternalSlot] = {
    obj.Slot(5).Equipment match {
      case Some(equip) =>
        BuildDetailedEquipment(5, equip) :: Nil
      case _ =>
        Nil
    }
  }

  /**
    * A builder method for turning an object into `0x17` decoded packet form.
    * @param index the position of the object
    * @param equip the game object
    * @return the game object in decoded packet form
    */
  private def BuildEquipment(index : Int, equip : Equipment) : InternalSlot = {
    InternalSlot(equip.Definition.ObjectId, equip.GUID, index, equip.Definition.Packet.ConstructorData(equip).get)
  }

  /**
    * A builder method for turning an object into `0x18` decoded packet form.
    * @param index the position of the object
    * @param equip the game object
    * @return the game object in decoded packet form
    */
  def BuildDetailedEquipment(index : Int, equip : Equipment) : InternalSlot = {
    InternalSlot(equip.Definition.ObjectId, equip.GUID, index, equip.Definition.Packet.DetailedConstructorData(equip).get)
  }

  /**
    * Given some equipment holsters, convert the contents of those holsters into converted-decoded packet data.
    * @param iter an `Iterator` of `EquipmentSlot` objects that are a part of the player's holsters
    * @param builder the function used to transform to the decoded packet form
    * @param list the current `List` of transformed data
    * @param index which holster is currently being explored
    * @return the `List` of inventory data created from the holsters
    */
  @tailrec private def recursiveMakeHolsters(iter : Iterator[EquipmentSlot], builder : ((Int, Equipment) => InternalSlot), list : List[InternalSlot] = Nil, index : Int = 0) : List[InternalSlot] = {
    if(!iter.hasNext) {
      list
    }
    else {
      val slot : EquipmentSlot = iter.next
      if(slot.Equipment.isDefined) {
        val equip : Equipment = slot.Equipment.get
        recursiveMakeHolsters(
          iter,
          builder,
          list :+ builder(index, equip),
          index + 1
        )
      }
      else {
        recursiveMakeHolsters(iter, builder, list, index + 1)
      }
    }
  }

  /**
    * Resolve which holster the player has drawn, if any.
    * @param obj the `Player` game object
    * @return the holster's Enumeration value
    */
  def GetDrawnSlot(obj : Player) : DrawnSlot.Value = {
    try { DrawnSlot(obj.DrawnSlot) } catch { case _ : Exception => DrawnSlot.None }
  }
}
