// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.builders

import akka.actor.Props
import net.psforever.objects.serverobject.locks.{IFFLock, IFFLockControl, IFFLockDefinition}

/**
  * Wrapper `Class` designed to instantiate a door lock server object that is sensitive to user faction affiliation.
  * @param idef a `IFFLockDefinition` object, indicating the specific functionality
  * @param id the globally unique identifier to which this `IFFLock` will be registered
  */
class IFFLockObjectBuilder(private val idef : IFFLockDefinition, private val id : Int) extends ServerObjectBuilder[IFFLock] {
  import akka.actor.ActorContext
  import net.psforever.objects.guid.NumberPoolHub

  def Build(implicit context : ActorContext, guid : NumberPoolHub) : IFFLock = {
    val obj = IFFLock(idef)
    guid.register(obj, id) //non-Actor GUID registration
    obj.Actor = context.actorOf(Props(classOf[IFFLockControl], obj), s"${idef.Name}_${obj.GUID.guid}")
    obj
  }
}

object IFFLockObjectBuilder {
  /**
    * Overloaded constructor for a `IFFLockObjectBuilder`.
    * @param idef an `IFFLock` object
    * @param id a globally unique identifier
    * @return an `IFFLockObjectBuilder` object
    */
  def apply(idef : IFFLockDefinition, id : Int) : IFFLockObjectBuilder = {
    new IFFLockObjectBuilder(idef, id)
  }
}
