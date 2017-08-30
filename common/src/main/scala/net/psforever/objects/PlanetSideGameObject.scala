// Copyright (c) 2017 PSForever
package net.psforever.objects

import net.psforever.objects.definition.ObjectDefinition
import net.psforever.objects.entity.{IdentifiableEntity, SimpleWorldEntity, WorldEntity}
import net.psforever.types.Vector3

/**
  * A basic class that indicates an entity that exists somewhere in the world and has a globally unique identifier.
  */
abstract class PlanetSideGameObject extends IdentifiableEntity with WorldEntity {
  private var entity : WorldEntity = new SimpleWorldEntity()

  def Entity : WorldEntity = entity

  def Entity_=(newEntity : WorldEntity) : Unit = {
    entity = newEntity
  }

  def Position : Vector3 = Entity.Position

  def Position_=(vec : Vector3) : Vector3 = {
    Entity.Position = vec
  }

  def Orientation : Vector3 = Entity.Orientation

  def Orientation_=(vec : Vector3) : Vector3 = {
    Entity.Orientation = vec
  }

  def Velocity : Option[Vector3] = Entity.Velocity

  def Velocity_=(vec : Option[Vector3]) : Option[Vector3] = {
    Entity.Velocity = vec
  }

  def Definition : ObjectDefinition
}

object PlanetSideGameObject {
  def toString(obj : PlanetSideGameObject) : String = {
    val guid : String = try { obj.GUID.guid.toString } catch { case _ : Exception => "NOGUID" }
    val P = obj.Position
    s"[$guid](x,y,z=${P.x%.3f},${P.y%.3f},${P.z%.3f})"
  }
}
