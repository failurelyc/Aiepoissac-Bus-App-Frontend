package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusStop
import com.aiepoissac.busapp.data.businfo.BusStopInfo

data class BusArrivalUIState(
    val busArrivalData: BusStop? = null,
    val busStopInfo: BusStopInfo? = null,
    val busStopCodeInput: String = "",
    val isRefreshing: Boolean = false
)
