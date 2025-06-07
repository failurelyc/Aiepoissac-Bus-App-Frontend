package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo

data class HomePageUIState (
    val busStopCodeInput: String = "",
    val busStopSearchResult: List<BusStopInfo> = listOf(),
    val busStopSearchBarExpanded: Boolean = false,
    val busServiceInput: String = "",
    val busServiceSearchResult: List<BusServiceInfoWithBusStopInfo> = listOf(),
    val busServiceSearchBarExpanded: Boolean = false
)