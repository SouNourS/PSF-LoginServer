// Copyright (c) 2017 PSForever
package net.psforever.objects.definition.converter

import net.psforever.objects.ConstructionItem
import net.psforever.packet.game.PlanetSideGUID
import net.psforever.packet.game.objectcreate.{CommonFieldData, DetailedConstructionToolData, HandheldData}
import net.psforever.types.PlanetSideEmpire

import scala.util.{Success, Try}

class ACEConverter extends ObjectCreateConverter[ConstructionItem]() {
  override def ConstructorData(obj : ConstructionItem) : Try[HandheldData] = {
    Success(
      HandheldData(
        CommonFieldData(
          PlanetSideEmpire.NEUTRAL, //TODO associate with a faction
          false,
          false,
          true,
          None,
          false,
          None,
          None,
          PlanetSideGUID(0)
        )
      )
    )
  }

  override def DetailedConstructorData(obj : ConstructionItem) : Try[DetailedConstructionToolData] = {
    Success(
      DetailedConstructionToolData(
        CommonFieldData(PlanetSideEmpire.NEUTRAL, false, false, true, None, false, None, None, PlanetSideGUID(0))
      )
    )
  }
}
