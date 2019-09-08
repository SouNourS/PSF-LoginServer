// Copyright (c) 2017 PSForever
import akka.actor.ActorContext
import net.psforever.objects.GlobalDefinitions
import net.psforever.objects.serverobject.resourcesilo.ResourceSilo
import net.psforever.objects.serverobject.structures.WarpGate
import net.psforever.objects.zones.Zone
import net.psforever.types.PlanetSideEmpire


object Zones {
  val z1 = new Zone("z1", Maps.map1, 1) {// Solsar
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(5).get.Name = Some("Seth")
      BuildingByMapId(6).get.Name = Some("Bastet")
      BuildingByMapId(7).get.Name = Some("Aton")
      BuildingByMapId(8).get.Name = Some("Hapi")
      BuildingByMapId(9).get.Name = Some("Thoth")
      BuildingByMapId(10).get.Name = Some("Mont")
      BuildingByMapId(11).get.Name = Some("Amun")
      BuildingByMapId(12).get.Name = Some("Horus")
      BuildingByMapId(13).get.Name = Some("Sobek")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.TR
      }
      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(19998).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(19999).get.Faction = PlanetSideEmpire.NEUTRAL
      //    BuildingByMapId(1).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
      //    BuildingByMapId(2).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
      //    BuildingByMapId(3).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
      //    BuildingByMapId(4).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(19998).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(19999).get.asInstanceOf[WarpGate].Active = false
    }
  }

  val z2 = new Zone("z2", Maps.map2, 2) {// Hossin
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(5).get.Name = Some("Voltan")
      BuildingByMapId(6).get.Name = Some("Naum")
      BuildingByMapId(7).get.Name = Some("Zotz")
      BuildingByMapId(8).get.Name = Some("Acan")
      BuildingByMapId(9).get.Name = Some("Bitol")
      BuildingByMapId(10).get.Name = Some("Chac")
      BuildingByMapId(11).get.Name = Some("Ghanon")
      BuildingByMapId(12).get.Name = Some("Ixtab")
      BuildingByMapId(13).get.Name = Some("Kisin")
      BuildingByMapId(14).get.Name = Some("Mulac")
      BuildingByMapId(48).get.Name = Some("Hurakan")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.TR
      }
      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(18907).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(18908).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(18907).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(18908).get.asInstanceOf[WarpGate].Active = false
    }
  }

  val z3 = new Zone("z3", Maps.map3, 3) {// Cyssor
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(1).get.Name = Some("Aja")
      BuildingByMapId(2).get.Name = Some("Bomazi")
      BuildingByMapId(4).get.Name = Some("Chuku")
      BuildingByMapId(5).get.Name = Some("Ekera")
      BuildingByMapId(6).get.Name = Some("Faro")
      BuildingByMapId(7).get.Name = Some("Wele")
      BuildingByMapId(8).get.Name = Some("Itan")
      BuildingByMapId(10).get.Name = Some("Leza")
      BuildingByMapId(11).get.Name = Some("Tore")
      BuildingByMapId(12).get.Name = Some("Nzame")
      BuildingByMapId(14).get.Name = Some("Orisha")
      BuildingByMapId(15).get.Name = Some("Pamba")
      BuildingByMapId(16).get.Name = Some("Shango")
      BuildingByMapId(18).get.Name = Some("Gunuku")
      BuildingByMapId(19).get.Name = Some("Honsi")
      BuildingByMapId(20).get.Name = Some("Kaang")
      BuildingByMapId(21).get.Name = Some("Mukuru")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.NEUTRAL
      }

      BuildingByMapId(18).get.Faction = PlanetSideEmpire.TR // Gunuku
      BuildingByMapId(8).get.Faction = PlanetSideEmpire.NC
      BuildingByMapId(20).get.Faction = PlanetSideEmpire.VS

      BuildingByMapId(32).get.Faction = PlanetSideEmpire.TR
      BuildingByMapId(33).get.Faction = PlanetSideEmpire.TR
      BuildingByMapId(43).get.Faction = PlanetSideEmpire.VS
      BuildingByMapId(44).get.Faction = PlanetSideEmpire.VS
      BuildingByMapId(45).get.Faction = PlanetSideEmpire.NC

      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(9).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(13).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(17).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(25936).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(25937).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(9).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(13).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(17).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(25936).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(25937).get.asInstanceOf[WarpGate].Active = false

    }
  }

  val z4 = new Zone("z4", Maps.map4, 4) {// Ishundar
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      import net.psforever.types.PlanetSideEmpire

      // PTS v3
      BuildingByMapId(5).get.Name = Some("Akkan")
      BuildingByMapId(6).get.Name = Some("Baal")
      BuildingByMapId(7).get.Name = Some("Dagon")
      BuildingByMapId(8).get.Name = Some("Enkidu")
      BuildingByMapId(9).get.Name = Some("Girru")
      BuildingByMapId(10).get.Name = Some("Hanish")
      BuildingByMapId(11).get.Name = Some("Irkalla")
      BuildingByMapId(12).get.Name = Some("Kusag")
      BuildingByMapId(13).get.Name = Some("Lahar")
      BuildingByMapId(14).get.Name = Some("Marduk")
      BuildingByMapId(15).get.Name = Some("Neti")
      BuildingByMapId(16).get.Name = Some("Zaqar")

      //      BuildingByMapId(5).get.Faction = PlanetSideEmpire.TR //Akkan
      //      BuildingByMapId(6).get.Faction = PlanetSideEmpire.TR //Baal
      //      BuildingByMapId(7).get.Faction = PlanetSideEmpire.TR //Dagon
      //      BuildingByMapId(8).get.Faction = PlanetSideEmpire.NC //Enkidu
      //      BuildingByMapId(9).get.Faction = PlanetSideEmpire.VS //Girru
      //      BuildingByMapId(10).get.Faction = PlanetSideEmpire.VS //Hanish
      //      BuildingByMapId(11).get.Faction = PlanetSideEmpire.VS //Irkalla
      //      BuildingByMapId(12).get.Faction = PlanetSideEmpire.VS //Kusag
      //      BuildingByMapId(13).get.Faction = PlanetSideEmpire.VS //Lahar
      //      BuildingByMapId(14).get.Faction = PlanetSideEmpire.NC //Marduk
      //      BuildingByMapId(15).get.Faction = PlanetSideEmpire.NC //Neti
      //      BuildingByMapId(16).get.Faction = PlanetSideEmpire.NC //Zaqar
      //      BuildingByMapId(17).get.Faction = PlanetSideEmpire.NC //S_Marduk_Tower
      //      BuildingByMapId(18).get.Faction = PlanetSideEmpire.NC //W_Neti_Tower
      //      BuildingByMapId(19).get.Faction = PlanetSideEmpire.NC //W_Zaqar_Tower
      //      BuildingByMapId(20).get.Faction = PlanetSideEmpire.NC //E_Zaqar_Tower
      //      BuildingByMapId(21).get.Faction = PlanetSideEmpire.NC //NE_Neti_Tower
      //      BuildingByMapId(22).get.Faction = PlanetSideEmpire.NC //SE_Ceryshen_Warpgate_Tower
      //      BuildingByMapId(23).get.Faction = PlanetSideEmpire.VS //S_Kusag_Tower
      //      BuildingByMapId(24).get.Faction = PlanetSideEmpire.VS //NW_Kusag_Tower
      //      BuildingByMapId(25).get.Faction = PlanetSideEmpire.VS //N_Ceryshen_Warpgate_Tower
      //      BuildingByMapId(26).get.Faction = PlanetSideEmpire.VS //SE_Irkalla_Tower
      //      BuildingByMapId(27).get.Faction = PlanetSideEmpire.VS //S_Irkalla_Tower
      //      BuildingByMapId(28).get.Faction = PlanetSideEmpire.TR //NE_Enkidu_Tower
      //      BuildingByMapId(29).get.Faction = PlanetSideEmpire.NC //SE_Akkan_Tower
      //      BuildingByMapId(30).get.Faction = PlanetSideEmpire.NC //SW_Enkidu_Tower
      //      BuildingByMapId(31).get.Faction = PlanetSideEmpire.TR //E_Searhus_Warpgate_Tower
      //      BuildingByMapId(32).get.Faction = PlanetSideEmpire.TR //N_Searhus_Warpgate_Tower
      //      BuildingByMapId(33).get.Faction = PlanetSideEmpire.VS //E_Girru_Tower
      //      BuildingByMapId(34).get.Faction = PlanetSideEmpire.VS //SE_Hanish_Tower
      //      BuildingByMapId(35).get.Faction = PlanetSideEmpire.TR //SW_Hanish_Tower
      //      BuildingByMapId(36).get.Faction = PlanetSideEmpire.VS //W_Girru_Tower
      //      BuildingByMapId(37).get.Faction = PlanetSideEmpire.TR //E_Dagon_Tower
      //      BuildingByMapId(38).get.Faction = PlanetSideEmpire.TR //NE_Baal_Tower
      //      BuildingByMapId(39).get.Faction = PlanetSideEmpire.TR //SE_Baal_Tower
      //      BuildingByMapId(40).get.Faction = PlanetSideEmpire.TR //S_Dagon_Tower
      //      BuildingByMapId(41).get.Faction = PlanetSideEmpire.NC //W_Ceryshen_Warpgate_Tower
      //      BuildingByMapId(42).get.Faction = PlanetSideEmpire.NEUTRAL //dagon bunker
      //      BuildingByMapId(43).get.Faction = PlanetSideEmpire.NEUTRAL //Akkan North Bunker
      //      BuildingByMapId(44).get.Faction = PlanetSideEmpire.NEUTRAL //Enkidu East Bunker
      //      BuildingByMapId(45).get.Faction = PlanetSideEmpire.NEUTRAL //Neti bunker
      //      BuildingByMapId(46).get.Faction = PlanetSideEmpire.NEUTRAL //Hanish West Bunker
      //      BuildingByMapId(47).get.Faction = PlanetSideEmpire.NEUTRAL //Irkalla East Bunker
      //      BuildingByMapId(48).get.Faction = PlanetSideEmpire.NEUTRAL //Zaqar bunker
      //      BuildingByMapId(49).get.Faction = PlanetSideEmpire.NEUTRAL //Kusag West Bunker
      //      BuildingByMapId(50).get.Faction = PlanetSideEmpire.NEUTRAL //marduk bunker
      //      BuildingByMapId(51).get.Faction = PlanetSideEmpire.TR //baal bunker
      //      BuildingByMapId(52).get.Faction = PlanetSideEmpire.NEUTRAL //girru bunker
      //      BuildingByMapId(53).get.Faction = PlanetSideEmpire.NEUTRAL //lahar bunker-
      //      BuildingByMapId(54).get.Faction = PlanetSideEmpire.NEUTRAL //akkan bunker
      //      BuildingByMapId(55).get.Faction = PlanetSideEmpire.VS //Irkalla_Tower
      //      BuildingByMapId(56).get.Faction = PlanetSideEmpire.VS //Hanish_Tower
      //      BuildingByMapId(57).get.Faction = PlanetSideEmpire.VS //E_Ceryshen_Warpgate_Tower
      //      BuildingByMapId(58).get.Faction = PlanetSideEmpire.VS //Lahar_Tower
      //      BuildingByMapId(59).get.Faction = PlanetSideEmpire.VS //VSSanc_Warpgate_Tower
      //      BuildingByMapId(60).get.Faction = PlanetSideEmpire.TR //Akkan_Tower
      //      BuildingByMapId(61).get.Faction = PlanetSideEmpire.NC //TRSanc_Warpgate_Tower
      //      BuildingByMapId(62).get.Faction = PlanetSideEmpire.NC //Marduk_Tower
      //      BuildingByMapId(63).get.Faction = PlanetSideEmpire.TR //NW_Dagon_Tower
      //      BuildingByMapId(64).get.Faction = PlanetSideEmpire.NEUTRAL //E7 East Bunker (at north from bridge)
      //      BuildingByMapId(65).get.Faction = PlanetSideEmpire.VS //W_Hanish_Tower

      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.TR
      }

      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(26620).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(26621).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(26620).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(26621).get.asInstanceOf[WarpGate].Active = false
    }
  }

  val z5 = new Zone("z5", Maps.map5, 5) {// Forseral
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(5).get.Name = Some("Ogma")
      BuildingByMapId(6).get.Name = Some("Neit")
      BuildingByMapId(7).get.Name = Some("Lugh")
      BuildingByMapId(8).get.Name = Some("Gwydion")
      BuildingByMapId(9).get.Name = Some("Dagda")
      BuildingByMapId(10).get.Name = Some("Pwyll")
      BuildingByMapId(11).get.Name = Some("Anu")
      BuildingByMapId(12).get.Name = Some("Bel")
      BuildingByMapId(13).get.Name = Some("Eadon")
      BuildingByMapId(36).get.Name = Some("Caer")

      BuildingByMapId(14).get.Name = Some("twr1")
      BuildingByMapId(17).get.Name = Some("twr2")
      BuildingByMapId(37).get.Name = Some("twr3")
      BuildingByMapId(27).get.Name = Some("twr4")
      BuildingByMapId(25).get.Name = Some("twr5")
      BuildingByMapId(39).get.Name = Some("twr6")
      BuildingByMapId(20).get.Name = Some("twr7")
      BuildingByMapId(22).get.Name = Some("twr8")
      BuildingByMapId(15).get.Name = Some("twr9")
      BuildingByMapId(26).get.Name = Some("twr10")
      BuildingByMapId(38).get.Name = Some("twr11")
      BuildingByMapId(18).get.Name = Some("twr12")
      BuildingByMapId(21).get.Name = Some("twr13")
      BuildingByMapId(16).get.Name = Some("twr14")
      BuildingByMapId(24).get.Name = Some("twr15")
      BuildingByMapId(19).get.Name = Some("twr16")
      BuildingByMapId(23).get.Name = Some("twr17")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.NEUTRAL
      }
      BuildingByMapId(7).get.Faction = PlanetSideEmpire.NC
      BuildingByMapId(8).get.Faction = PlanetSideEmpire.VS
      BuildingByMapId(9).get.Faction = PlanetSideEmpire.TR

      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(21074).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(21078).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(21074).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(21078).get.asInstanceOf[WarpGate].Active = false
    }
  }

  val z6 = new Zone("z6", Maps.map6, 6) {// Ceryshen
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }
      //      GUID(2094) match {
      //        case Some(silo : ResourceSilo) =>
      //          silo.ChargeLevel = silo.MaximumCharge
      //        case _ => ;
      //      }

      BuildingByMapId(1).get.Name = Some("Akna")
      BuildingByMapId(2).get.Name = Some("Anguta")
      BuildingByMapId(3).get.Name = Some("Igaluk")
      BuildingByMapId(4).get.Name = Some("Keelut")
      BuildingByMapId(5).get.Name = Some("Nerrivik")
      BuildingByMapId(6).get.Name = Some("Pinga")
      BuildingByMapId(7).get.Name = Some("Sedna")
      BuildingByMapId(8).get.Name = Some("Tarqaq")
      BuildingByMapId(9).get.Name = Some("Tootega")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.VS
      }
      BuildingByMapId(10).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(11).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(12).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(13).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(18657).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(18658).get.Faction = PlanetSideEmpire.NEUTRAL

      //      BuildingByMapId(2).get.Faction = PlanetSideEmpire.VS
      BuildingByMapId(10).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(11).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(12).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(13).get.asInstanceOf[WarpGate].Broadcast = true
      //      BuildingByMapId(48).get.Faction = PlanetSideEmpire.VS
      //      BuildingByMapId(49).get.Faction = PlanetSideEmpire.VS
      BuildingByMapId(18657).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(18658).get.asInstanceOf[WarpGate].Active = false

    }
  }

  val z7 = new Zone("z7", Maps.map7, 7) {// Esamir
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(5).get.Name = Some("Andvari")
      BuildingByMapId(6).get.Name = Some("Dagur")
      BuildingByMapId(7).get.Name = Some("Eisa")
      BuildingByMapId(8).get.Name = Some("Freyr")
      BuildingByMapId(9).get.Name = Some("Gjallar")
      BuildingByMapId(10).get.Name = Some("Helheim")
      BuildingByMapId(11).get.Name = Some("Kvasir")
      BuildingByMapId(12).get.Name = Some("Mani")
      BuildingByMapId(13).get.Name = Some("Nott")
      BuildingByMapId(14).get.Name = Some("Vidar")
      BuildingByMapId(15).get.Name = Some("Ymir")
      BuildingByMapId(16).get.Name = Some("Jarl")
      BuildingByMapId(17).get.Name = Some("Ran")

      import net.psforever.types.PlanetSideEmpire
      //      Buildings.values.foreach { _.Faction = PlanetSideEmpire.NC }
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.NEUTRAL
      }

      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(7).get.Faction = PlanetSideEmpire.VS
      BuildingByMapId(8).get.Faction = PlanetSideEmpire.TR
      BuildingByMapId(14).get.Faction = PlanetSideEmpire.NC
      BuildingByMapId(21321).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(21322).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(21321).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(21322).get.asInstanceOf[WarpGate].Active = false

    }
  }

  val z8 = new Zone("z8", Maps.map8, 8) {// Oshur
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      import net.psforever.types.PlanetSideEmpire

      BuildingByMapId(5).get.Name = Some("Atar")
      BuildingByMapId(6).get.Name = Some("Dahaka")
      BuildingByMapId(7).get.Name = Some("Hvar")
      BuildingByMapId(8).get.Name = Some("Izha")
      BuildingByMapId(9).get.Name = Some("Jamshid")
      BuildingByMapId(10).get.Name = Some("Mithra")
      BuildingByMapId(11).get.Name = Some("Rashnu")
      BuildingByMapId(12).get.Name = Some("Yazata")
      BuildingByMapId(13).get.Name = Some("Zal")

      BuildingByMapId(5).get.Faction = PlanetSideEmpire.VS //Atar
      BuildingByMapId(6).get.Faction = PlanetSideEmpire.NC //Dahaka
      BuildingByMapId(7).get.Faction = PlanetSideEmpire.NC //Hvar
      BuildingByMapId(8).get.Faction = PlanetSideEmpire.NC //Izha
      BuildingByMapId(9).get.Faction = PlanetSideEmpire.TR //Jamshid
      BuildingByMapId(10).get.Faction = PlanetSideEmpire.TR //Mithra
      BuildingByMapId(11).get.Faction = PlanetSideEmpire.TR //Rashnu
      BuildingByMapId(12).get.Faction = PlanetSideEmpire.VS //Yazata
      BuildingByMapId(13).get.Faction = PlanetSideEmpire.VS //Zal

      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(24811).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(24815).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(24811).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(24815).get.asInstanceOf[WarpGate].Active = false
    }
  }

  val z9 = new Zone("z9", Maps.map9, 9) {// Searhus
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(4).get.Name = Some("Akua")
      BuildingByMapId(5).get.Name = Some("Drakulu")
      BuildingByMapId(6).get.Name = Some("Hiro")
      BuildingByMapId(7).get.Name = Some("Iva")
      BuildingByMapId(8).get.Name = Some("Karihi")
      BuildingByMapId(9).get.Name = Some("Laka")
      BuildingByMapId(10).get.Name = Some("Matagi")
      BuildingByMapId(11).get.Name = Some("Ngaru")
      BuildingByMapId(12).get.Name = Some("Oro")
      BuildingByMapId(13).get.Name = Some("Pele")
      BuildingByMapId(14).get.Name = Some("Rehua")
      BuildingByMapId(15).get.Name = Some("Sina")
      BuildingByMapId(16).get.Name = Some("Tara")
      BuildingByMapId(17).get.Name = Some("Wakea")

      BuildingByMapId(47).get.Name = Some("twr1")
      BuildingByMapId(20).get.Name = Some("twr2")
      BuildingByMapId(18).get.Name = Some("twr3")
      BuildingByMapId(26).get.Name = Some("twr4")
      BuildingByMapId(46).get.Name = Some("twr5")
      BuildingByMapId(48).get.Name = Some("twr6")
      BuildingByMapId(51).get.Name = Some("twr7")
      BuildingByMapId(44).get.Name = Some("twr8")
      BuildingByMapId(52).get.Name = Some("twr9")
      BuildingByMapId(53).get.Name = Some("twr10")
      BuildingByMapId(45).get.Name = Some("twr11")
      BuildingByMapId(19).get.Name = Some("twr12")
      BuildingByMapId(28).get.Name = Some("twr13")
      BuildingByMapId(24).get.Name = Some("twr14")
      BuildingByMapId(49).get.Name = Some("twr15")
      BuildingByMapId(21).get.Name = Some("twr16")
      BuildingByMapId(22).get.Name = Some("twr16")
      BuildingByMapId(23).get.Name = Some("twr17")
      BuildingByMapId(25).get.Name = Some("twr18")
      BuildingByMapId(29).get.Name = Some("twr19")
      BuildingByMapId(37).get.Name = Some("twr20")
      BuildingByMapId(27).get.Name = Some("twr21")
      BuildingByMapId(50).get.Name = Some("twr22")

      import net.psforever.types.PlanetSideEmpire

      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.NEUTRAL
      }

      BuildingByMapId(13).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(11).get.Faction = PlanetSideEmpire.TR
      BuildingByMapId(12).get.Faction = PlanetSideEmpire.VS

      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(23717).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(23718).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(23717).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(23718).get.asInstanceOf[WarpGate].Active = false

    }
  }

  val z10 = new Zone("z10", Maps.map10, 10) {// Amerish
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      import net.psforever.types.PlanetSideEmpire

      BuildingByMapId(5).get.Name = Some("Azeban")
      BuildingByMapId(6).get.Name = Some("Cetan")
      BuildingByMapId(7).get.Name = Some("Heyoka")
      BuildingByMapId(8).get.Name = Some("Ikanam")
      BuildingByMapId(9).get.Name = Some("Kyoi")
      BuildingByMapId(10).get.Name = Some("Mekala")
      BuildingByMapId(11).get.Name = Some("Onatha")
      BuildingByMapId(12).get.Name = Some("Qumu")
      BuildingByMapId(13).get.Name = Some("Sungrey")
      BuildingByMapId(14).get.Name = Some("Tumas")
      BuildingByMapId(15).get.Name = Some("Xelas")
      BuildingByMapId(49).get.Name = Some("Verica")

      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.NC
      }

      BuildingByMapId(5).get.Faction = PlanetSideEmpire.TR
      BuildingByMapId(11).get.Faction = PlanetSideEmpire.VS

      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NC
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(4).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(20900).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(20902).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(2).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(3).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(4).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(20900).get.asInstanceOf[WarpGate].Active = false
      BuildingByMapId(20902).get.asInstanceOf[WarpGate].Active = false

    }
  }

  val home1 = new Zone("home1", Maps.map11, 11) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.NC
      }
      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.NC
      BuildingByMapId(2).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.NC
      BuildingByMapId(3).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.NC
    }
  }

  val home2 = new Zone("home2", Maps.map12, 12) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.TR
      }
      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(1).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
      BuildingByMapId(2).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
      BuildingByMapId(3).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.TR
    }
  }

  val home3 = new Zone("home3", Maps.map13, 13) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.VS
      }
      BuildingByMapId(1).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.NEUTRAL

      BuildingByMapId(60).get.Faction = PlanetSideEmpire.NC //South Villa Gun Tower
      BuildingByMapId(1).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.VS
      BuildingByMapId(2).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.VS
      BuildingByMapId(3).get.asInstanceOf[WarpGate].BroadcastFor = PlanetSideEmpire.VS
    }
  }

  val tzshtr = new Zone("tzshtr", Maps.map14, 14)

  val tzdrtr = new Zone("tzdrtr", Maps.map15, 15)

  val tzcotr = new Zone("tzcotr", Maps.map16, 16)

  val tzshnc = new Zone("tzshnc", Maps.map14, 17)

  val tzdrnc = new Zone("tzdrnc", Maps.map15, 18)

  val tzconc = new Zone("tzconc", Maps.map16, 19)

  val tzshvs = new Zone("tzshvs", Maps.map14, 20)

  val tzdrvs = new Zone("tzdrvs", Maps.map15, 21)

  val tzcovs = new Zone("tzcovs", Maps.map16, 22)

  val c1 = new Zone("c1", Maps.ugd01, 23) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules
    }
  }

  val c2 = new Zone("c2", Maps.ugd02, 24) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules
    }
  }

  val c3 = new Zone("c3", Maps.ugd03, 25) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      //        import net.psforever.types.PlanetSideEmpire
      //        BuildingByMapId(10359).get.Faction = PlanetSideEmpire.TR //Redoubt SE
      //        BuildingByMapId(10359).ModelId = 104
    }
  }

  val c4 = new Zone("c4", Maps.ugd04, 26) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules
    }
  }

  val c5 = new Zone("c5", Maps.ugd05, 27) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules
    }
  }

  val c6 = new Zone("c6", Maps.ugd06, 28) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules
    }
  }

  val i1 = new Zone("i1", Maps.map99, 29) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(7).get.Name = Some("Mithra")
      BuildingByMapId(8).get.Name = Some("Hvar")
      BuildingByMapId(18).get.Name = Some("Yazata")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.VS
      }
      BuildingByMapId(10461).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10462).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10464).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10461).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10462).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10464).get.asInstanceOf[WarpGate].Broadcast = true
    }
  }

  val i2 = new Zone("i2", Maps.map98, 30) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(7).get.Name = Some("Zal")
      BuildingByMapId(8).get.Name = Some("Rashnu")
      BuildingByMapId(39).get.Name = Some("Sraosha")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.VS
      }
      BuildingByMapId(10000).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10001).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10002).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10000).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10001).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10002).get.asInstanceOf[WarpGate].Broadcast = true
    }
  }

  val i3 = new Zone("i3", Maps.map97, 31) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(1).get.Name = Some("Dahaka")
      BuildingByMapId(2).get.Name = Some("Jamshid")
      BuildingByMapId(3).get.Name = Some("Izha")

      BuildingByMapId(6).get.Name = Some("twr1")
      BuildingByMapId(4).get.Name = Some("twr2")
      BuildingByMapId(5).get.Name = Some("twr3")
      BuildingByMapId(7).get.Name = Some("twr4")
      BuildingByMapId(8).get.Name = Some("twr5")
      BuildingByMapId(9).get.Name = Some("twr6")
      BuildingByMapId(10).get.Name = Some("twr7")

      import net.psforever.types.PlanetSideEmpire
//      Buildings.values.foreach {
//        _.Faction = PlanetSideEmpire.VS
//      }
      BuildingByMapId(1).get.Faction = PlanetSideEmpire.TR
      BuildingByMapId(2).get.Faction = PlanetSideEmpire.NC
      BuildingByMapId(3).get.Faction = PlanetSideEmpire.VS

      BuildingByMapId(10001).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10002).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10003).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10001).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10002).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10003).get.asInstanceOf[WarpGate].Broadcast = true
    }
  }

  val i4 = new Zone("i4", Maps.map96, 32) {
    override def Init(implicit context: ActorContext): Unit = {
      super.Init(context)
      HotSpotCoordinateFunction = Zones.HotSpots.StandardRemapping(Map.Scale, 80, 80)
      HotSpotTimeFunction = Zones.HotSpots.StandardTimeRules

      Buildings.values.flatMap {
        _.Amenities.collect {
          case amenity if amenity.Definition == GlobalDefinitions.resource_silo =>
            val silo = amenity.asInstanceOf[ResourceSilo]
            silo.ChargeLevel = silo.MaximumCharge
        }
      }

      BuildingByMapId(1).get.Name = Some("Atar")
      BuildingByMapId(2).get.Name = Some("North_Rim_Gun_Tower")
      BuildingByMapId(3).get.Name = Some("East_Rim_Gun_Tower")
      BuildingByMapId(4).get.Name = Some("South_Rim_Gun_Tower")
      BuildingByMapId(5).get.Name = Some("L8_Gate_Watch_Tower")
      BuildingByMapId(6).get.Name = Some("K14_Gate_Watch_Tower")

      import net.psforever.types.PlanetSideEmpire
      Buildings.values.foreach {
        _.Faction = PlanetSideEmpire.VS
      }
      BuildingByMapId(10001).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10002).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10001).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10002).get.asInstanceOf[WarpGate].Broadcast = true
      BuildingByMapId(10000).get.Faction = PlanetSideEmpire.NEUTRAL
      BuildingByMapId(10000).get.asInstanceOf[WarpGate].Active = false

    }
  }

  /**
    * Get the zone identifier name for the sanctuary continent of a given empire.
    *
    * @param faction the empire
    * @return the zone id, with a blank string as an invalidating result
    */
  def SanctuaryZoneId(faction: PlanetSideEmpire.Value): String = {
    faction match {
      case PlanetSideEmpire.NC => "home1"
      case PlanetSideEmpire.TR => "home2"
      case PlanetSideEmpire.VS => "home3"
      case PlanetSideEmpire.NEUTRAL => "" //invalid, not black ops
    }
  }

  /**
    * Get the zone number for the sanctuary continent of a given empire.
    *
    * @param faction the empire
    * @return the zone number, within the sequence 1-32, and with 0 as an invalidating result
    */
  def SanctuaryZoneNumber(faction: PlanetSideEmpire.Value): Int = {
    faction match {
      case PlanetSideEmpire.NC => 11
      case PlanetSideEmpire.TR => 12
      case PlanetSideEmpire.VS => 13
      case PlanetSideEmpire.NEUTRAL => 0 //invalid, not black ops
    }
  }

  /**
    * Given a zone identification string, provide that zone's ordinal number.
    * As zone identification naming is extremely formulaic,
    * just being able to poll the zone's identifier by its first few letters will produce its ordinal position.
    *
    * @param id a zone id string
    * @return a zone number
    */
  def NumberFromId(id: String): Int = {
    if (id.startsWith("z")) {
      //z2 -> 2
      id.substring(1).toInt
    }
    else if (id.startsWith("home")) {
      //home2 -> 2 + 10 = 12
      id.substring(4).toInt + 10
    }
    else if (id.startsWith("tz")) {
      //tzconc -> (14 + (3 * 1) + 2) -> 19
      (List("tr", "nc", "vs").indexOf(id.substring(4)) * 3) + List("sh", "dr", "co").indexOf(id.substring(2, 4)) + 14
    }
    else if (id.startsWith("c")) {
      //c2 -> 2 + 21 = 23
      id.substring(1).toInt + 21
    }
    else if (id.startsWith("i")) {
      //i2 -> 2 + 28 = 30
      id.substring(1).toInt + 28
    }
    else {
      0
    }
  }

  object HotSpots {

    import net.psforever.objects.ballistics.SourceEntry
    import net.psforever.objects.zones.MapScale
    import net.psforever.types.Vector3

    import scala.concurrent.duration._

    /**
      * Produce hotspot coordinates based on map coordinates.
      *
      * @see `FindClosestDivision`
      * @param scale      the map's scale (width and height)
      * @param longDivNum the number of division lines spanning the width of the `scale`
      * @param latDivNum  the number of division lines spanning the height of the `scale`
      * @param pos        the absolute position of the activity reported
      * @return the position for a hotspot
      */
    def StandardRemapping(scale: MapScale, longDivNum: Int, latDivNum: Int)(pos: Vector3): Vector3 = {
      Vector3(
        //x
        FindClosestDivision(pos.x, scale.width, longDivNum),
        //y
        FindClosestDivision(pos.y, scale.height, latDivNum),
        //z is always zero - maps are flat 2D planes
        0
      )
    }

    /**
      * Produce hotspot coordinates based on map coordinates.<br>
      * <br>
      * Transform a reported number by mapping it
      * into a division from a regular pattern of divisions
      * defined by the scale divided evenly a certain number of times.
      * The depicted number of divisions is actually one less than the parameter number
      * as the first division is used to represent everything before that first division (there is no "zero").
      * Likewise, the last division occurs before the farther edge of the scale is counted
      * and is used to represent everything after that last division.
      * This is not unlike rounding.
      *
      * @param coordinate the point to scale
      * @param scale      the map's scale (width and height)
      * @param divisions  the number of division lines spanning across the `scale`
      * @return the closest regular division
      */
    private def FindClosestDivision(coordinate: Float, scale: Float, divisions: Float): Float = {
      val divLength: Float = scale / divisions
      if (coordinate >= scale - divLength) {
        scale - divLength
      }
      else if (coordinate >= divLength) {
        val sector: Float = (coordinate * divisions / scale).toInt * divLength
        val nextSector: Float = sector + divLength
        if (coordinate - sector < nextSector - coordinate) {
          sector
        }
        else {
          nextSector
        }
      }
      else {
        divLength
      }
    }

    /**
      * Determine a duration for which the hotspot will be displayed on the zone map.
      * Friendly fire is not recognized.
      *
      * @param defender the defending party
      * @param attacker the attacking party
      * @return the duration
      */
    def StandardTimeRules(defender: SourceEntry, attacker: SourceEntry): FiniteDuration = {
      import net.psforever.objects.ballistics._
      import net.psforever.objects.GlobalDefinitions
      if (attacker.Faction == defender.Faction) {
        0 seconds
      }
      else {
        //TODO is target occupy-able and occupied, or jammer-able and jammered?
        defender match {
          case _: PlayerSource =>
            60 seconds
          case _: VehicleSource =>
            60 seconds
          case t: ObjectSource if t.Definition == GlobalDefinitions.manned_turret =>
            60 seconds
          case _: DeployableSource =>
            30 seconds
          case _: ComplexDeployableSource =>
            30 seconds
          case _ =>
            0 seconds
        }
      }
    }
  }
}