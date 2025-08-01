package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo

/**
 * This class stores the UI state for the Bus Route Information page.
 *
 * @param busRoute The route list displayed.
 * @param busStopSequenceOffset The bus stop sequence of the 0th bus stop of the
 * displayed route list in the original bus route
 * @param originalBusRoute The full route list of the bus service
 * @param busServiceInfo The bus service information
 * @param showBusServiceInfo Whether the bus service information should be shown
 * @param busServiceVariants A list containing all bus service variants of the current bus service
 * @param truncated Whether the displayed route list is not the same as the original route list
 * @param firstStopIsStartOfLoopingPoint Whether the 0th stop of the displayed route is
 * the first stop of the looping point
 * @param showFirstLastBus Whether the first/last bus timings for all stops
 * in the route list should be shown
 * @param isLiveLocation Whether live user location and speed should be constantly fetched and shown on the map
 * @param currentSpeed The speed of the user to be displayed
 * @param showMap Whether the map is shown
 * @param showLiveBuses Whether bus arrival data of the 0th stop should be shown
 * @param liveBuses The bus arrival data at the 0th stop
 */
data class BusRouteUIState (
    val busRoute: List<Pair<Int, BusRouteInfoWithBusStopInfo>> = listOf(),
    val busStopSequenceOffset: Int = 0,
    val originalBusRoute: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val busServiceInfo: BusServiceInfo? = null,
    val showBusServiceInfo: Boolean = false,
    val busServiceVariants: List<BusServiceInfoWithBusStopInfo> = listOf(),
    val truncated: Boolean = false,
    val firstStopIsStartOfLoopingPoint: Boolean = false,
    val showFirstLastBus: Boolean = false,
    val isLiveLocation: Boolean = false,
    val currentSpeed: Int = 0,
    val showMap: Boolean = false,
    val showLiveBuses: Boolean = false,
    val liveBuses: List<Bus> = listOf()
)