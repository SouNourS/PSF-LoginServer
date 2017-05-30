// Copyright (c) 2017 PSForever
package net.psforever.packet.game.objectcreate

import net.psforever.packet.Marshallable
import net.psforever.packet.game.PlanetSideGUID
import scodec.codecs._
import scodec.{Attempt, Codec, Err}
import shapeless.{::, HNil}

/**
  * A representation of a generic vehicle, with optional mounted weapons.
  * This data will help construct most of the game's vehicular options such as the Lightning or the Harasser.<br>
  * <br>
  * Vehicles utilize their own packet to communicate position to the server, known as `VehicleStateMessage`.
  * This takes the place of `PlayerStateMessageUpstream` when the player avatar is in control;
  * and, it takes the place of `PlayerStateMessage` for other players when they are in control.
  * If the vehicle is sufficiently complicated, a `ChildObjectStateMessage` will be used.
  * This packet will control any turret(s) on the vehicle.
  * For very complicated vehicles, the packets `FrameVehicleStateMessage` and `VehicleSubStateMessage` will also be employed.
  * The tasks that these packets perform are different based on the vehicle that responds or generates them.<br>
  * <br>
  * Vehicles have a variety of features.
  * They have their own inventory space, seating space for driver and passengers, Infantry mounting positions for the former two, and weapon mounting positions.
  * Specialized vehicles also have terminals attached to them.
  * The trunk is little different from player character inventories save for capacity and that it must be manually accessed.
  * It is usually on the rear of the vehicle if that vehicle has a trunk at all.
  * Weapons and infantry are allocated mounting slots from the same list.
  * Weapons are constructed in their given slot with the vehicle itself and Infantry sit aside the weapons.
  * Certain slots ("seats") allow control of one of the weapons in another slot ("weapon mounting").
  * ("Seat" and "weapon mounting" do not coincide numerically.)
  * For trunk and for Infantry slots, various glyphs are projected onto the ground, called "mounting positions."
  * Standing nearly on top of the glyph and facing the vehicle allows access or seat-taking.
  * ("Seat" and "mounting positions" will not necessarily coincide numerically either.)<br>
  * <br>
  * Outside of managing mounted weaponry, any vehicle with special "utilities" must be handled as a special case.
  * Utilities are plastered onto the chassis and carried around with the vehicle.
  * Some vehicles have to go through a sessile physical conversion known as "deploying" to get access to their utilities.<br>
  * <br>
  * An "expected" number of mounting data can be passed into the class for the purposes of validating input.
  * @param basic data common to objects
  * @param unk1 na
  * @param health the amount of health the vehicle has, as a percentage of a filled bar
  * @param unk2 na
  * @param unk3 na
  * @param unk4 na
  * @param unk5 na;
  *             1 causes the `quadstealth` (Wraith) to cloak
  * @param mountings data regarding the mounted utilities, usually weapons
  * @param mount_capacity implicit;
  *                       the total number of mounted utilities allowed on this vehicle;
  *                       defaults to 1;
  *                       -1 or less ignores the imposed checks
  */
final case class VehicleData(basic : CommonFieldData,
                             unk1 : Int,
                             health : Int,
                             unk2 : Int,
                             unk3 : Int,
                             unk4 : Boolean,
                             unk5 : Int,
                             mountings : Option[List[InternalSlot]] = None
                            )(implicit val mount_capacity : Int = 1) extends ConstructorData {
  override def bitsize : Long = {
    val basicSize = basic.bitsize
    val internalSize = if(mountings.isDefined) {
      var bSize : Long = 0L
      for(item <- mountings.get) {
        bSize += item.bitsize
      }
      10 + bSize
    }
    else {
      0L
    }
    3L + VehicleData.baseVehicleSize + basicSize + internalSize
  }
}

object VehicleData extends Marshallable[VehicleData] {
  val baseVehicleSize : Long = 21L //2u + 8u + 2u + 8u + 1u

  /**
    * Overloaded constructor that mandates information about a single weapon mount.
    * @param basic data common to objects
    * @param health the amount of health the object has, as a percentage of a filled bar
    * @param mount data regarding the mounted weapon
    * @return a `VehicleData` object
    */
  def apply(basic : CommonFieldData, health : Int, mount : InternalSlot) : VehicleData =
    VehicleData(basic, 0, health, 0, 0, false, 0, Some(mount :: Nil))

  /**
    * Perform an evaluation of the provided object.
    * @param list a List of objects to be compared against some criteria
    * @return `true`, if the objects pass this test; false, otherwise
    */
  def allAllowed(list : List[InternalSlot]) : Boolean = true

  /**
    * A `Codec` for mounted utilities, generally weapons (as `WeaponData`).
    * @param slotChecker a function that takes a `List` of `InternalSlot` objects and returns `true` if those objects passed its test;
    *                    defaults to `allAllowed`
    * @return a `List` of mounted objects or a `BitVector` of the same
    * @see `InventoryData`
    */
  def mountedUtilitiesCodec(slotChecker : (List[InternalSlot]) => Boolean = allAllowed) : Codec[List[InternalSlot]] =
    InventoryData.codec(InternalSlot.codec).exmap[List[InternalSlot]] (
    {
      case InventoryData(list) =>
        if(!slotChecker(list)) {
          Attempt.failure(Err("vehicle mount decoding is disallowed by test failure"))
        }
        else {
          Attempt.successful(list)
        }

      case _ =>
        Attempt.failure(Err("invalid mounting data format"))
    },
    {
      case list =>
        if(list.size > 255) {
          Attempt.failure(Err("vehicle encodes too many weapon mountings (255+ types!)"))
        }
        else if(!slotChecker(list)) {
          Attempt.failure(Err("vehicle mount encoding is disallowed by test failure"))
        }
        else {
          Attempt.successful(InventoryData(list))
        }
    }
  )

  /**
    * This pattern translates between the listed datatypes and the bitstream encoding defined in `basic_vehicle_codec`.
    */
  type basicVehiclePattern = CommonFieldData :: Int :: Int :: Int :: Int :: Boolean :: HNil

  /**
    * These values are parsed by all vehicles.
    * Comments about the fields are provided where helpful.
    */
  val basic_vehicle_codec : Codec[basicVehiclePattern] = (
    CommonFieldData.codec :: //not certain if player_guid is valid
      uint2L :: //often paired with the assumed 16u field in previous?
      uint8L :: //usually "health"
      uint2L :: //usually 0; second bit turns off vehicle seat entry points
      uint8L :: //special field (AMS and ANT use for deploy state)
      bool //unknown but generally false
    ).as[basicVehiclePattern]

  /**
    * A `Codec` for `VehicleData`.
    * @param mount_capacity the total number of mounted weapons that are attached to this vehicle;
    *                       defaults to 1
    * @param typeCheck implicit;
    *                  a function that takes an object and returns `true` if the object passed its defined test;
    *                  defaults to `allAllowed`
    * @return a `VehicleData` object or a `BitVector`
    */
  def codec(mount_capacity : Int = 1)(implicit typeCheck : (List[InternalSlot]) => Boolean = allAllowed) : Codec[VehicleData] = (
    basic_vehicle_codec.exmap[basicVehiclePattern] (
      {
        case basic :: u1 :: health :: u2 :: u3 :: u4 :: HNil =>
          Attempt.successful(basic :: u1 :: health :: u2 :: u3 :: u4 :: HNil)
      },
      {
        case basic :: u1 :: health :: u2 :: u3 :: u4 :: HNil =>
          Attempt.successful(basic :: u1 :: health :: u2 :: u3 :: u4 :: HNil)
      }
    ) :+
      uint2L :+
      optional(bool, "mountings" | mountedUtilitiesCodec())
    ).exmap[VehicleData] (
    {
      case basic :: u1 :: health :: u2 :: u3 :: u4 :: u5 :: mountings :: HNil =>
        val onboardMountCount : Int = if(mountings.isDefined) { mountings.get.size } else { 0 }
        if(mount_capacity > -1 && mount_capacity != onboardMountCount) {
          Attempt.failure(Err(s"vehicle decodes wrong number of mounts - actual $onboardMountCount, expected $mount_capacity"))
        }
        else {
          Attempt.successful(VehicleData(basic, u1, health, u2, u3, u4, u5, mountings)(onboardMountCount))
        }

      case _ =>
        Attempt.failure(Err("invalid vehicle data format"))
    },
    {
      case obj @ VehicleData(basic, u1, health, u2, u3, u4, u5, mountings) =>
        val objMountCapacity = obj.mount_capacity
        if(objMountCapacity < 0 || mount_capacity < 0) {
          Attempt.successful(basic :: u1 :: health :: u2 :: u3 :: u4 :: u5 :: mountings :: HNil)
        }
        else {
          val onboardMountCount : Int = if(mountings.isDefined) { mountings.get.size } else { 0 }
          if(mount_capacity != objMountCapacity) {
            Attempt.failure(Err(s"different encoding expectations for amount of mounts - actual $objMountCapacity, expected $mount_capacity"))
          }
          else if(mount_capacity != onboardMountCount) {
            Attempt.failure(Err(s"vehicle encodes wrong number of mounts - actual $onboardMountCount, expected $mount_capacity"))
          }
          else {
            Attempt.successful(basic :: u1 :: health :: u2 :: u3 :: u4 :: u5 :: mountings :: HNil)
          }
        }

      case _ =>
        Attempt.failure(Err("invalid vehicle data format"))
    }
  )

  implicit val codec : Codec[VehicleData] = codec()

  /**
    * A compilation of the `VehicleData` object that would be used for each stock vehicle.
    * Each function is named after the `ObjectClass` name (internal name) of the vehicle it creates.
    */
  object Prefab {
    def ams(basic : CommonFieldData, deployState : AMSDeployState.Value, matrix_guid : PlanetSideGUID, respawn_guid : PlanetSideGUID, term_a_guid : PlanetSideGUID, term_b_guid : PlanetSideGUID)(implicit health : Int = 255) : AMSData = {
      AMSData(basic, health, deployState, matrix_guid, respawn_guid, term_a_guid, term_b_guid)
    }

    def ant(basic : CommonFieldData, deployState : ANTDeployState.Value)(implicit health : Int = 255) : ANTData = {
      ANTData(basic, health, deployState)
    }

    def aurora(basic : CommonFieldData, weapon1_guid : PlanetSideGUID, ammo11_guid : PlanetSideGUID, ammo12_guid : PlanetSideGUID, weapon2_guid : PlanetSideGUID, ammo21_guid : PlanetSideGUID, ammo22_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, 0, health, 0, 0x7, true, 0,
        Some(
          InternalSlot(ObjectClass.aurora_weapon_systema, weapon1_guid, 5,
            WeaponData(0xC, 0x8, 0, ObjectClass.fluxpod_ammo, ammo11_guid, 0, AmmoBoxData(0x8), ObjectClass.fluxpod_ammo, ammo12_guid, 1, AmmoBoxData(0x8))
          ) ::
            InternalSlot(ObjectClass.aurora_weapon_systemb, weapon2_guid, 6,
              WeaponData(0xC, 0x8, 0, ObjectClass.fluxpod_ammo, ammo21_guid, 0, AmmoBoxData(0x8), ObjectClass.fluxpod_ammo, ammo22_guid, 1, AmmoBoxData(0x8))
            ) :: Nil
        )
      )(2)
    }

    def basilisk(basic : CommonFieldData, weapon_guid : PlanetSideGUID, ammo_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, health,
        InternalSlot(ObjectClass.quadassault_weapon_system, weapon_guid, 1,
          WeaponData(0xC, 0x8, ObjectClass.bullet_12mm, ammo_guid, 0, AmmoBoxData(0x8))
        )
      )
    }

    def battlewagon(basic : CommonFieldData, weapon1_guid : PlanetSideGUID, ammo1_guid : PlanetSideGUID, weapon2_guid : PlanetSideGUID, ammo2_guid : PlanetSideGUID, weapon3_guid : PlanetSideGUID, ammo3_guid : PlanetSideGUID, weapon4_guid : PlanetSideGUID, ammo4_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, 0, health, 0, 0x7, true, 0,
        Some(
          InternalSlot(ObjectClass.battlewagon_weapon_systema, weapon1_guid, 5,
            WeaponData(0xC, 0x8, 0, ObjectClass.bullet_15mm, ammo1_guid, 0, AmmoBoxData(0x8))
          ) ::
            InternalSlot(ObjectClass.battlewagon_weapon_systemb, weapon2_guid, 6,
              WeaponData(0xC, 0x8, 0, ObjectClass.bullet_15mm, ammo2_guid, 0, AmmoBoxData(0x8))
            ) ::
            InternalSlot(ObjectClass.battlewagon_weapon_systemc, weapon3_guid, 7,
              WeaponData(0xC, 0x8, 0, ObjectClass.bullet_15mm, ammo3_guid, 0, AmmoBoxData(0x8))
            ) ::
            InternalSlot(ObjectClass.battlewagon_weapon_systemd, weapon4_guid, 8,
              WeaponData(0xC, 0x8, 0, ObjectClass.bullet_15mm, ammo4_guid, 0, AmmoBoxData(0x8))
            ) :: Nil
        )
      )(4)
    }

    def fury(basic : CommonFieldData, weapon_guid : PlanetSideGUID, ammo_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, health,
        InternalSlot(ObjectClass.fury_weapon_systema, weapon_guid, 1,
          WeaponData(0xC, 0x8, ObjectClass.hellfire_ammo, ammo_guid, 0, AmmoBoxData(0x8))
        )
      )
    }

    def harasser(basic : CommonFieldData, weapon_guid : PlanetSideGUID, ammo_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, health,
        InternalSlot(ObjectClass.chaingun_p, weapon_guid, 2,
          WeaponData(0xC, 0x8, ObjectClass.bullet_12mm, ammo_guid, 0, AmmoBoxData(0x8))
        )
      )
    }

    def lightning(basic : CommonFieldData, weapon_guid : PlanetSideGUID, ammo1_guid : PlanetSideGUID, ammo2_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, health,
        InternalSlot(ObjectClass.lightning_weapon_system, weapon_guid, 1,
          WeaponData(0x8, 0x8, 0, ObjectClass.bullet_75mm, ammo1_guid, 0, AmmoBoxData(0x0), ObjectClass.bullet_25mm, ammo2_guid, 1, AmmoBoxData(0x0))
        )
      )
    }

    def mediumtransport(basic : CommonFieldData, weapon1_guid : PlanetSideGUID, ammo1_guid : PlanetSideGUID, weapon2_guid : PlanetSideGUID, ammo2_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, 0, health, 0, 0x7, true, 0,
        Some(
          InternalSlot(ObjectClass.mediumtransport_weapon_systemA, weapon1_guid, 5,
            WeaponData(0xC, 0x8, ObjectClass.bullet_20mm, ammo1_guid, 0, AmmoBoxData(0x8))
          ) ::
            InternalSlot(ObjectClass.mediumtransport_weapon_systemB, weapon2_guid, 6,
              WeaponData(0xC, 0x8, ObjectClass.bullet_20mm, ammo2_guid, 0, AmmoBoxData(0x8))
            ) :: Nil
        )
      )(2)
    }

    def threemanheavybuggy(basic : CommonFieldData, weapon1_guid : PlanetSideGUID, ammo1_guid : PlanetSideGUID, weapon2_guid : PlanetSideGUID, ammo2_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, 0, health, 0, 0x7, true, 0,
        Some(
          InternalSlot(ObjectClass.chaingun_p, weapon1_guid, 3,
            WeaponData(0xC, 0x8, 0, ObjectClass.bullet_12mm, ammo1_guid, 0, AmmoBoxData(0x8))
          ) ::
            InternalSlot(ObjectClass.grenade_launcher_marauder, weapon2_guid, 4,
              WeaponData(0xC, 0x8, 0, ObjectClass.heavy_grenade_mortar, ammo2_guid, 0, AmmoBoxData(0x8))
            ) :: Nil
        )
      )(2)
    }

    def thunderer(basic : CommonFieldData, weapon1_guid : PlanetSideGUID, ammo1_guid : PlanetSideGUID, weapon2_guid : PlanetSideGUID, ammo2_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, 0, health, 0, 0x7, true, 0,
        Some(
          InternalSlot(ObjectClass.thunderer_weapon_systema, weapon1_guid, 5,
            WeaponData(0xC, 0x8, 0, ObjectClass.gauss_cannon_ammo, ammo1_guid, 0, AmmoBoxData(0x8))
          ) ::
            InternalSlot(ObjectClass.thunderer_weapon_systemb, weapon2_guid, 6,
              WeaponData(0xC, 0x8, 0, ObjectClass.gauss_cannon_ammo, ammo2_guid, 0, AmmoBoxData(0x8))
            ) :: Nil
        )
      )(2)
    }

    def twomanheavybuggy(basic : CommonFieldData, weapon_guid : PlanetSideGUID, ammo_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, health,
        InternalSlot(ObjectClass.advanced_missile_launcher_t, weapon_guid, 2,
          WeaponData(0xC, 0x8, 0, ObjectClass.firebird_missile, ammo_guid, 0, AmmoBoxData(0x8))
        )
      )
    }

    def twomanhoverbuggy(basic : CommonFieldData, weapon_guid : PlanetSideGUID, ammo_guid : PlanetSideGUID)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, health,
        InternalSlot(ObjectClass.flux_cannon_thresher, weapon_guid, 2,
          WeaponData(0xC, 0x8, 0, ObjectClass.flux_cannon_thresher_battery, ammo_guid, 0, AmmoBoxData(0x8))
        )
      )
    }

    def wraith(basic : CommonFieldData)(implicit health : Int = 255) : VehicleData = {
      VehicleData(basic, 0, health, 0, 0, false, 0)(0)
    }
  }
}
