package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.userdata.BusJourneyInfo

data class SavedJourneyUIState (
    val busJourneys: List<Pair<BusJourneyInfo, Pair<Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>, Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>>>>
        = listOf(),
    val showAddDialog: Boolean = false,
    val serviceNoInput: String = "",
    val directionOne: Boolean = true,
    val originStopInput: BusRouteInfoWithBusStopInfo? = null,
    val destinationStopInput: BusRouteInfoWithBusStopInfo? = null,
    val originStopSearchResults: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val destinationStopSearchResults: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val originStopSearchExpanded: Boolean = false,
    val destinationStopSearchExpanded: Boolean = false
)