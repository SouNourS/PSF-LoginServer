// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.terminals

import net.psforever.objects.Player
import net.psforever.objects.inventory.InventoryItem
import net.psforever.packet.game.ItemTransactionMessage
import net.psforever.objects.serverobject.terminals.EquipmentTerminalDefinition._

/**
  * The definition for any `Terminal` that is of a type "order_terminal".
  * This kind of "order_terminal" is applicable to facilities.<br>
  * <br>
  * `Buy` and `Sell` `Equipment` items and `AmmoBox` items.
  * Change `ExoSuitType` and retrieve `Loadout` entries.
  */
class OrderTerminalDefinition extends EquipmentTerminalDefinition(612) {
  Name = "order_terminal"

  /**
    * The `Equipment` available from this `Terminal` on specific pages.
    */
  private val buyFunc : (Player, ItemTransactionMessage)=>Terminal.Exchange = EquipmentTerminalDefinition.Buy(
    infantryAmmunition ++ infantryWeapons,
    supportAmmunition ++ supportWeapons,
    suits ++ maxSuits)

  override def Buy(player: Player, msg : ItemTransactionMessage) : Terminal.Exchange = buyFunc(player, msg)

  /**
    * Process a `TransactionType.InfantryLoadout` action by the user.
    * `Loadout` objects are blueprints composed of exo-suit specifications and simplified `Equipment`-to-slot mappings.
    * If a valid loadout is found, its data is transformed back into actual `Equipment` for return to the user.
    * @param player the player
    * @param msg the original packet carrying the request
    * @return an actionable message that explains how to process the request
    */
  override def Loadout(player : Player, msg : ItemTransactionMessage) : Terminal.Exchange = {
    if(msg.item_page == 4) { //Favorites tab
      player.LoadLoadout(msg.unk1) match {
        case Some(loadout) =>
          val holsters = loadout.VisibleSlots.map(entry => { InventoryItem(BuildSimplifiedPattern(entry.item), entry.index) })
          val inventory = loadout.Inventory.map(entry => { InventoryItem(BuildSimplifiedPattern(entry.item), entry.index) })
          Terminal.InfantryLoadout(loadout.ExoSuit, loadout.Subtype, holsters, inventory)
        case None =>
          Terminal.NoDeal()
      }
    }
    else {
      Terminal.NoDeal()
    }
  }
}
