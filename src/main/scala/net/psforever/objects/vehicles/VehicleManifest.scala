// Copyright (c) 2020 PSForever
package net.psforever.objects.vehicles

import net.psforever.objects.Vehicle
import net.psforever.objects.zones.Zone

/**
  * na
  * @param file the id of this manifest entry;
  *             used as the channel name for summoning passengers to the vehicle
  *             after it has been loaded to a new location or to a new zone;
  *             this channel name should be unique to the vehicle for at least the duration of the transition;
  *             the vehicle-specific channel with which all passengers are coordinated back to the original vehicle
  * @param vehicle the vehicle in transport
  * @param origin where the vehicle originally was
  * @param driverName the name of the driver when the transport process started
  * @param passengers the paired names and seat indices of all passengers when the transport process started
  * @param cargo the paired driver names and cargo hold indices of all cargo vehicles when the transport process started
  */
final case class VehicleManifest(
    file: String,
    vehicle: Vehicle,
    origin: Zone,
    driverName: String,
    passengers: List[(String, Int)],
    cargo: List[(String, Int)]
)

object VehicleManifest {
  def apply(vehicle: Vehicle): VehicleManifest = {
    val driverName = vehicle.Seats(0).Occupant match {
      case Some(driver) => driver.Name
      case None         => "MISSING_DRIVER"
    }
    val passengers = vehicle.Seats.collect {
      case (index, seat) if index > 0 && seat.isOccupied =>
        (seat.Occupant.get.Name, index)
    }
    val cargo = vehicle.CargoHolds.collect {
      case (index, hold) if hold.Occupant.nonEmpty =>
        hold.Occupant.get.Seats(0).Occupant match {
          case Some(driver) =>
            (driver.Name, index)
          case None =>
            ("MISSING_DRIVER", index)
        }
    }
    VehicleManifest(ManifestChannelName(vehicle), vehicle, vehicle.Zone, driverName, passengers.toList, cargo.toList)
  }

  def ManifestChannelName(vehicle: Vehicle): String = {
    s"transport-vehicle-channel-${vehicle.GUID.guid}"
  }
}
