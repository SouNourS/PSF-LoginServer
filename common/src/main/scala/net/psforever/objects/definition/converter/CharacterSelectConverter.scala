// Copyright (c) 2017 PSForever
package net.psforever.objects.definition.converter

import net.psforever.objects.{EquipmentSlot, Player}
import net.psforever.objects.equipment.Equipment
import net.psforever.packet.game.objectcreate.{BasicCharacterData, CharacterAppearanceData, CharacterData, DetailedCharacterData, ImplantEntry, InternalSlot, InventoryData, PlacementData, RibbonBars}
import net.psforever.types.{GrenadeState, ImplantType}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
  * `CharacterSelectConverter` is a simplified `AvatarConverter`
  * that is tailored for appearance of the player character on the character selection screen.
  * Details that would not be apparent on that screen such as implants or certifications are ignored.
  */
class CharacterSelectConverter extends AvatarConverter {
  override def ConstructorData(obj : Player) : Try[CharacterData] = Failure(new Exception("CharacterSelectConverter should not be used to generate CharacterData"))

  override def DetailedConstructorData(obj : Player) : Try[DetailedCharacterData] = {
    Success(
      DetailedCharacterData(
        MakeAppearanceData(obj),
        obj.BEP,
        obj.CEP,
        1, 1, 0, 1, 1,
        Nil,
        MakeImplantEntries(obj), //necessary for correct stream length
        Nil, Nil,
        MakeCosmetics(obj.BEP),
        InventoryData(recursiveMakeHolsters(obj.Holsters().iterator)),
        GetDrawnSlot(obj)
      )
    )
  }

  /**
    * Compose some data from a `Player` into a representation common to both `CharacterData` and `DetailedCharacterData`.
    * @param obj the `Player` game object
    * @see `AvatarConverter.MakeAppearanceData`
    * @return the resulting `CharacterAppearanceData`
    */
  private def MakeAppearanceData(obj : Player) : CharacterAppearanceData = {
    CharacterAppearanceData(
      PlacementData(0f, 0f, 0f),
      BasicCharacterData(obj.Name, obj.Faction, obj.Sex, obj.Head, 1),
      0,
      false,
      false,
      obj.ExoSuit,
      "",
      0,
      false,
      0f,
      0f,
      true,
      GrenadeState.None,
      false,
      false,
      false,
      RibbonBars()
    )
  }

  /**
    * Transform an `Array` of `Implant` objects into a `List` of `ImplantEntry` objects suitable as packet data.
    * @param obj the `Player` game object
    * @return the resulting implant `List`
    * @see `ImplantEntry` in `DetailedCharacterData`
    */
  private def MakeImplantEntries(obj : Player) : List[ImplantEntry] = {
    List.fill[ImplantEntry](DetailedCharacterData.numberOfImplantSlots(obj.BEP))(ImplantEntry(ImplantType.None, None))
  }

  /**
    * Given some equipment holsters, convert the contents of those holsters into converted-decoded packet data.
    * @param iter an `Iterator` of `EquipmentSlot` objects that are a part of the player's holsters
    * @param list the current `List` of transformed data
    * @param index which holster is currently being explored
    * @see `AvatarConverter.recursiveMakeHolsters`
    * @return the `List` of inventory data created from the holsters
    */
  @tailrec private def recursiveMakeHolsters(iter : Iterator[EquipmentSlot], list : List[InternalSlot] = Nil, index : Int = 0) : List[InternalSlot] = {
    if(!iter.hasNext) {
      list
    }
    else {
      val slot : EquipmentSlot = iter.next
      if(slot.Equipment.isDefined) {
        val equip : Equipment = slot.Equipment.get
        recursiveMakeHolsters(
          iter,
          list :+ BuildDetailedEquipment(index, equip),
          index + 1
        )
      }
      else {
        recursiveMakeHolsters(iter, list, index + 1)
      }
    }
  }
}
