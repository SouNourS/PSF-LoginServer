// Copyright (c) 2017 PSForever
package net.psforever.packet.game.objectcreate

import InventoryItem._
import net.psforever.packet.PacketHelpers
import scodec.Codec
import scodec.codecs._
import shapeless.{::, HNil}

/**
  * A representation of the inventory portion of `ObjectCreate*Message` packet data for avatars.<br>
  * <br>
  * The inventory is a temperamental thing.
  * Items placed into the inventory must follow their proper encoding schematics to the letter.
  * The slot number refers to the position occupied by the item.
  * In icon format, all-encompassing slots are absolute positions; and, grid-distributed icons use the upper-left corner.
  * No values are allowed to be misplaced and no unexpected regions of data can be discovered.
  * If there is even a minor failure, the remainder of the inventory will fail to translate.<br>
  * <br>
  * Inventories are usually prefaced with a `bin1` value not accounted for here.
  * It can be treated as optional.
  * @param contents the items in the inventory
  * @see `InventoryItem`
  */
final case class InventoryData(contents : List[InventoryItem] = List.empty) extends StreamBitSize {
  override def bitsize : Long = {
    val base : Long = 10L //8u + 1u + 1u
    var invSize : Long = 0L //length of all items in inventory
    for(item <- contents) {
      invSize += item.bitsize
    }
    base + invSize
  }
}

object InventoryData {
  private def inventoryCodec(itemCodec : Codec[InventoryItem]) : Codec[InventoryData] = (
    uint8L >>:~ { len =>
      uint2L ::
        ("contents" | PacketHelpers.listOfNSized(len, itemCodec))
    }
  ).xmap[InventoryData] (
    {
      case _ :: 0 :: c :: HNil =>
        InventoryData(c)
    },
    {
      case InventoryData(c) =>
        c.size :: 0 :: c :: HNil
    }
  )

  /**
    * A `Codec` for `0x17` `ObjectCreateMessage` data.
    */
  val codec : Codec[InventoryData] = inventoryCodec(InventoryItem.codec)

  /**
    * A `Codec` for `0x18` `ObjectCreateDetailedMessage` data.
    */
  val codec_detailed : Codec[InventoryData] = inventoryCodec(InventoryItem.codec_detailed)
}
