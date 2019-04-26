// Copyright (c) 2019 PSForever
package net.psforever.objects

import net.psforever.packet.game.PlanetSideGUID

trait OwnableByPlayer {
  private var owner : Option[PlanetSideGUID] = None
  private var ownerName : Option[String] = None

  def Owner : Option[PlanetSideGUID] = owner

  def Owner_=(owner : PlanetSideGUID) : Option[PlanetSideGUID] = Owner_=(Some(owner))

  def Owner_=(owner : Player) : Option[PlanetSideGUID] = Owner_=(Some(owner.GUID))

  def Owner_=(owner : Option[PlanetSideGUID]) : Option[PlanetSideGUID] = {
    owner match {
      case Some(_) =>
        this.owner = owner
      case None =>
        this.owner = None
    }
    Owner
  }

  def OwnerName : Option[String] = ownerName

  def OwnerName_=(owner : String) : Option[String] = OwnerName_=(Some(owner))

  def OwnerName_=(owner : Player) : Option[String] = OwnerName_=(Some(owner.Name))

  def OwnerName_=(owner : Option[String]) : Option[String] = {
    owner match {
      case Some(_) =>
        ownerName = owner
      case None =>
        ownerName = None
    }
    OwnerName
  }
}
