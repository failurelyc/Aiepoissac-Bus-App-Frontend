package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo

data class BusRouteUIState (
    val busRoute: List<Pair<Int, BusRouteInfoWithBusStopInfo>> = listOf(),
    val busStopSequenceOffset: Int = 0,
    val originalBusRoute: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val busServiceInfo: BusServiceInfo? = null,
    val busServiceVariants: List<BusServiceInfoWithBusStopInfo> = listOf(),
    val truncated: Boolean = false,
    val truncatedAfterLoopingPoint: Boolean = false,
    val showFirstLastBus: Boolean = false,
    val isLiveLocation: Boolean = false,
    val currentSpeed: Int = 0,
    val showMap: Boolean = false,
    val showLiveBuses: Boolean = false,
    val liveBuses: List<Bus> = listOf()
)