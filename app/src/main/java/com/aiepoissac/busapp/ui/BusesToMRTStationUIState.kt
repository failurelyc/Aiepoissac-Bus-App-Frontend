package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import java.time.DayOfWeek
import java.time.LocalTime

data class BusesToMRTStationUIState (
    val originalRoutes: List<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> = listOf(),
    val routes: List<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> = listOf(),
    val distanceThreshold: Int,
    val mrtStation: MRTStation? = null,
    val sortedByBusStop: Boolean = false,
    val sortedByNumberOfStops: Boolean = false,
    val sortedByWalkingDistance: Boolean = false,
    val currentTime: LocalTime = LocalTime.now(),
    val dayOfWeek: DayOfWeek = DayOfWeek.FRIDAY,
    val showOnlyOperatingBusServices: Boolean = false,
    val showTimeDial: Boolean = false,
    val searchingForBusServices: Boolean = false
)