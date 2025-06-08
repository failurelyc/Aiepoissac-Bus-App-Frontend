package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo

data class BusArrivalUIState(
    val busArrivalData: List<Pair<Pair<BusStopInfo?, BusStopInfo?>, BusService>>? = null,
    val busStopInfo: BusStopInfo? = null,
    val busRoutes: List<BusRouteInfo> = listOf(),
    val isRefreshing: Boolean = false,
    val showBusArrival: Boolean = true,
    val hideBusType: Boolean = false,
    val busStopCodeInput: String = "",
    val expanded: Boolean = false,
    val searchResult: List<BusStopInfo> = listOf()
)
