package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusStop
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo

data class BusArrivalUIState(
    val busArrivalData: BusStop? = null,
    val busStopInfo: BusStopInfo? = null,
    val busRoutes: List<BusRouteInfo> = listOf(),
    val isRefreshing: Boolean = false,
    val showBusArrival: Boolean = true,
    val busStopCodeInput: String = "",
    val expanded: Boolean = false,
    val searchResult: List<BusStopInfo> = listOf()
)
