// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.tube

import akka.actor.ActorContext
import net.psforever.objects.definition.ObjectDefinition
import net.psforever.objects.definition.converter.SpawnTubeConverter
import net.psforever.objects.serverobject.structures.Amenity

/**
  * The definition for any `VehicleSpawnPad`.
  * Currently, all tubes identify as object id 49 - `ams_respawn_tube` - configured manually.
  * @see `GlobalDefinitions.ams_respawn_tube`
  */
class SpawnTubeDefinition(object_id : Int) extends ObjectDefinition(object_id) {
  Name = if(object_id == 49) {
    "ams_respawn_tube"
  }
  else if(object_id == 732) {
    "respawn_tube"
  }
  else if(object_id == 733) {
    "respawn_tube_tower"
  }
  else {
    throw new IllegalArgumentException("terminal must be object id 49, 732, or 733")
  }
  Packet = new SpawnTubeConverter
}

object SpawnTubeDefinition {
  /**
    * Assemble some logic for a provided object.
    * @param obj an `Amenity` object;
    *            anticipating a `Terminal` object using this same definition
    * @param context hook to the local `Actor` system
    */
  def Setup(obj : Amenity, context : ActorContext) : Unit = {
    import akka.actor.{ActorRef, Props}
    if(obj.Actor == ActorRef.noSender) {
      obj.Actor = context.actorOf(Props(classOf[SpawnTubeControl], obj), s"${obj.Definition.Name}_${obj.GUID.guid}")
    }
  }
}
