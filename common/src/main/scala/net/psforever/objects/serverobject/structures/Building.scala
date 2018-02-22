// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.structures

import akka.actor.ActorContext
import net.psforever.objects.definition.ObjectDefinition
import net.psforever.objects.serverobject.PlanetSideServerObject
import net.psforever.objects.zones.Zone
import net.psforever.packet.game.PlanetSideGUID
import net.psforever.types.PlanetSideEmpire

class Building(private val id : Int, private val zone : Zone) extends PlanetSideServerObject {
  private var faction : PlanetSideEmpire.Value = PlanetSideEmpire.NEUTRAL
  private var amenities : List[Amenity] = List.empty
  GUID = PlanetSideGUID(0)

  def Id : Int = id

  def Faction : PlanetSideEmpire.Value = faction

  override def Faction_=(fac : PlanetSideEmpire.Value) : PlanetSideEmpire.Value = {
    faction = fac
    Faction
  }

  def Amenities : List[Amenity] = amenities

  def Amenities_=(obj : Amenity) : List[Amenity] = {
    amenities = amenities :+ obj
    obj.Owner = this
    amenities
  }

  def Zone : Zone = zone

  def Definition: ObjectDefinition = Building.BuildingDefinition
}

object Building {
  final val NoBuilding : Building = new Building(0, Zone.Nowhere) {
    override def Faction_=(faction : PlanetSideEmpire.Value) : PlanetSideEmpire.Value = PlanetSideEmpire.NEUTRAL
    override def Amenities_=(obj : Amenity) : List[Amenity] = Nil
  }

  final val BuildingDefinition : ObjectDefinition = new ObjectDefinition(0) { Name = "building" }

  def apply(id : Int, zone : Zone) : Building = {
    new Building(id, zone)
  }

  def Structure(id : Int, zone : Zone, context : ActorContext) : Building = {
    import akka.actor.Props
    val obj = new Building(id, zone)
    obj.Actor = context.actorOf(Props(classOf[BuildingControl], obj), s"$id-building")
    obj
  }
}
