package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import java.time.LocalDateTime

data class BusRouteUIState (
    val busRoute: List<Pair<Int, BusRouteInfoWithBusStopInfo>> = listOf(),
    val busStopSequenceOffset: Int = 0,
    val originalBusRoute: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val busServiceInfo: BusServiceInfo? = null,
    val truncated: Boolean = false,
    val truncatedAfterLoopingPoint: Boolean = false,
    val showFirstLastBus: Boolean = false,
    val isLiveLocation: Boolean = false,
    val lastTimeLocationUpdated: LocalDateTime = LocalDateTime.MIN,
    val currentSpeed: Int = 0
)