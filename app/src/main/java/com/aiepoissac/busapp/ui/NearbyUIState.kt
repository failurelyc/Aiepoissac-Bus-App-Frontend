package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.bicycle.BicycleParking
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import java.time.DayOfWeek
import java.time.LocalTime

data class NearbyUIState(
    val originalBusStopList: List<Pair<Int, BusStopInfo>> = listOf(),
    val busStopList: List<Pair<Int, BusStopInfoWithBusRoutesInfo>> = listOf(),
    val mrtStationList: List<Pair<Int, MRTStation>> = listOf(),
    val bicycleParkingList: List<Pair<Int, BicycleParking>>? = listOf(),
    val point: HasCoordinates,
    val distanceThreshold: Int,
    val busStopListLimit: Int,
    val isLiveLocation: Boolean,
    val showNearbyBusStops: Boolean = false,
    val showNearbyMRTStations: Boolean = false,
    val showNearbyBusStopsOnMap: Boolean = true,
    val showBicycleParkingOnMap: Boolean = false,
    val currentTime: LocalTime = LocalTime.now(),
    val dayOfWeek: DayOfWeek = DayOfWeek.FRIDAY,
    val showOnlyOperatingBusServices: Boolean = false,
    val showTimeDial: Boolean = false,
    val searchingForBusStops: Boolean = false
)
