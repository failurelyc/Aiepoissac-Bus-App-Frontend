package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo

data class BusRouteUIState (
    val busRoute: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val originalBusRoute: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val busServiceInfo: BusServiceInfo? = null,
    val truncated: Boolean = false,
    val truncatedAfterLoopingPoint: Boolean = false,
    val showFirstLastBus: Boolean = false
)