// Copyright (c) 2017 PSForever
package net.psforever.objects

import net.psforever.objects.equipment.EquipmentSize
import net.psforever.objects.inventory.InventoryTile
import net.psforever.types.ExoSuitType

/**
  * A definition for producing the personal armor the player wears.
  * Players are influenced by the exo-suit they wear in a variety of ways, with speed and available equipment slots being major differences.
  * @param suitType the `Enumeration` corresponding to this exo-suit
  */
class ExoSuitDefinition(private val suitType : ExoSuitType.Value) {
  private var permission : Int = 0 //TODO certification type?
  private var maxArmor : Int = 0
  private val holsters : Array[EquipmentSize.Value] = Array.fill[EquipmentSize.Value](5)(EquipmentSize.Blocked)
  private var inventoryScale : InventoryTile = InventoryTile.Tile11 //override with custom InventoryTile
  private var inventoryOffset : Int = 0

  def SuitType : ExoSuitType.Value = suitType

  def MaxArmor : Int = maxArmor

  def MaxArmor_=(armor : Int) : Int = {
    maxArmor = math.min(math.max(0, armor), 65535)
    MaxArmor
  }

  def InventoryScale : InventoryTile = inventoryScale

  def InventoryScale_=(scale : InventoryTile) : InventoryTile = {
    inventoryScale = scale
    InventoryScale
  }

  def InventoryOffset : Int = inventoryOffset

  def InventoryOffset_=(offset : Int) : Int = {
    inventoryOffset = offset
    InventoryOffset
  }

  def Holsters : Array[EquipmentSize.Value] = holsters

  def Holster(slot : Int) : EquipmentSize.Value = {
    if(slot >= 0 && slot < 5) {
      holsters(slot)
    }
    else {
      EquipmentSize.Blocked
    }
  }

  def Holster(slot : Int, value : EquipmentSize.Value) : EquipmentSize.Value = {
    if(slot >= 0 && slot < 5) {
      holsters(slot) = value
      holsters(slot)
    }
    else {
      EquipmentSize.Blocked
    }
  }
}

object ExoSuitDefinition {
  final val Standard = ExoSuitDefinition(ExoSuitType.Standard)
  Standard.MaxArmor = 50
  Standard.InventoryScale = InventoryTile.Tile96
  Standard.InventoryOffset = 6
  Standard.Holster(0, EquipmentSize.Pistol)
  Standard.Holster(2, EquipmentSize.Rifle)
  Standard.Holster(4, EquipmentSize.Melee)

  final val Agile = ExoSuitDefinition(ExoSuitType.Agile)
  Agile.MaxArmor = 100
  Agile.InventoryScale = InventoryTile.Tile99
  Agile.InventoryOffset = 6
  Agile.Holster(0, EquipmentSize.Pistol)
  Agile.Holster(1, EquipmentSize.Pistol)
  Agile.Holster(2, EquipmentSize.Rifle)
  Agile.Holster(4, EquipmentSize.Melee)

  final val Reinforced = ExoSuitDefinition(ExoSuitType.Reinforced)
  Reinforced.permission = 1
  Reinforced.MaxArmor = 200
  Reinforced.InventoryScale = InventoryTile.Tile1209
  Reinforced.InventoryOffset = 6
  Reinforced.Holster(0, EquipmentSize.Pistol)
  Reinforced.Holster(1, EquipmentSize.Pistol)
  Reinforced.Holster(2, EquipmentSize.Rifle)
  Reinforced.Holster(3, EquipmentSize.Rifle)
  Reinforced.Holster(4, EquipmentSize.Melee)

  final val Infiltration = ExoSuitDefinition(ExoSuitType.Standard)
  Infiltration.permission = 1
  Infiltration.MaxArmor = 0
  Infiltration.InventoryScale = InventoryTile.Tile66
  Infiltration.InventoryOffset = 6
  Infiltration.Holster(0, EquipmentSize.Pistol)
  Infiltration.Holster(4, EquipmentSize.Melee)

  final val MAX = ExoSuitDefinition(ExoSuitType.MAX)
  MAX.permission = 1
  MAX.MaxArmor = 650
  MAX.InventoryScale = InventoryTile.Tile1612
  MAX.InventoryOffset = 6
  MAX.Holster(2, EquipmentSize.Max)
  MAX.Holster(4, EquipmentSize.Melee)

  def apply(suitType : ExoSuitType.Value) : ExoSuitDefinition = {
    new ExoSuitDefinition(suitType)
  }

  /**
    * A function to retrieve the correct defintion of an exo-suit from the type of exo-suit.
    * @param suit the `Enumeration` corresponding to this exo-suit
    * @return the exo-suit definition
    */
  def Select(suit : ExoSuitType.Value) : ExoSuitDefinition = {
    suit match {
      case ExoSuitType.Agile => ExoSuitDefinition.Agile
      case ExoSuitType.Infiltration => ExoSuitDefinition.Infiltration
      case ExoSuitType.MAX => ExoSuitDefinition.MAX
      case ExoSuitType.Reinforced => ExoSuitDefinition.Reinforced
      case _ => ExoSuitDefinition.Standard
    }
  }
}
