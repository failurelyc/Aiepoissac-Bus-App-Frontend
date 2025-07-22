package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import com.aiepoissac.busapp.data.mrtstation.MRTStation

data class HomePageUIState (
    val busStopCodeInput: String = "",
    val busStopSearchResult: List<BusStopInfo> = listOf(),
    val busStopSearchBarExpanded: Boolean = false,
    val busStopRoadInput: String = "",
    val busStopRoadSearchResult: List<BusStopInfo> = listOf(),
    val busStopRoadSearchBarExpanded: Boolean = false,
    val busServiceInput: String = "",
    val busServiceSearchResult: List<BusServiceInfoWithBusStopInfo> = listOf(),
    val busServiceSearchBarExpanded: Boolean = false,
    val mrtStationInput: String = "",
    val mrtStationSearchResult: List<MRTStation> = listOf(),
    val mrtStationSearchBarExpanded: Boolean = false
)