package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.userdata.JourneySegmentInfo

/**
 * This class stores the UI state for the Saved Journey page.
 *
 * @param segments The list of all segments contained in the Saved Journey
 * @param showAddDialog Whether the Add Segment Dialog is shown
 * @param serviceNoInput The current bus service number input value
 * @param isDirectionTwo Whether the direction input is 2 or 1
 * @param originStopInput The current origin bus stop input value
 * @param destinationStopInput The current destination bus stop input value
 * @param originStopSearchResults The search results for the origin bus stop
 * @param destinationStopSearchResults The search results for the destination bus stop
 * @param originStopSearchExpanded Whether the origin bus stop search results are expanded
 * @param destinationStopSearchExpanded Whether the destination bus stop search results are expanded
 * @param showBusType Whether bus icons for each bus arrival should be shown
 * @param showFirstLastBus Whether first and last bus timings should be shown
 * @param showDestinationBusArrivals Whether the destination bus stop,
 * including the bus arrival data, should be shown.
 */
data class SavedJourneyUIState (
    val segments: List<Pair<JourneySegmentInfo, Pair<Pair<BusRouteInfoWithBusStopInfo, BusService?>, Pair<BusRouteInfoWithBusStopInfo, BusService?>>>>
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