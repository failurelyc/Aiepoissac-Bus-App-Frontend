package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.userdata.JourneySegmentInfo

data class SavedJourneyUIState (
    val busJourneys: List<Pair<JourneySegmentInfo, Pair<Pair<BusRouteInfoWithBusStopInfo, BusService?>, Pair<BusRouteInfoWithBusStopInfo, BusService?>>>>
        = listOf(),
    val showAddDialog: Boolean = false,
    val serviceNoInput: String = "",
    val isDirectionTwo: Boolean = false,
    val originStopInput: BusRouteInfoWithBusStopInfo? = null,
    val destinationStopInput: BusRouteInfoWithBusStopInfo? = null,
    val originStopSearchResults: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val destinationStopSearchResults: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val originStopSearchExpanded: Boolean = false,
    val destinationStopSearchExpanded: Boolean = false,
    val showBusType: Boolean = true,
    val showFirstLastBus: Boolean = false,
    val showDestinationBusArrivals: Boolean = false
)