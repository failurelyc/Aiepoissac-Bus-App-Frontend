package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusStop

data class BusArrivalUIState(
    val busArrivalData: BusStop? = null,
    val busStopCodeInput: String = "",
    val networkIssue: Boolean = false,
    val isRefreshing: Boolean = false
)
