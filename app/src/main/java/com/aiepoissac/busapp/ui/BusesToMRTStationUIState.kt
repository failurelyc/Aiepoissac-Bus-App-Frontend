package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.MRTStation

data class BusesToMRTStationUIState (
    val routes: List<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> = listOf(),
    val distanceThreshold: Int,
    val mrtStation: MRTStation? = null
)