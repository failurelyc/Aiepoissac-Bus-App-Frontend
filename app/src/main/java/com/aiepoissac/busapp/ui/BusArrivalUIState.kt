package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo

/**
 * This class stores the UI state for Bus Arrival and Bus Stop Information pages
 *
 * @param busArrivalData Bus arrival data to be displayed, or null if failed to obtain the data
 * @param busStopInfo The bus stop information of the current bus stop
 * @param busRoutes A list of all bus route information of bus services serving this stop
 * @param isRefreshing Whether new bus arrival data is being obtained
 * @param showBusArrival Whether Bus Arrival or Bus Stop Information should be shown
 * @param showBusType Whether bus icons for each bus arrival should be shown
 * @param showFirstLastBus Whether first and last bus timings should be shown
 * @param busStopCodeInput The current input value of the bus stop code input field
 * @param expanded Whether the search results are expanded
 * @param connectionIssue Whether the app failed to obtain bus arrival data
 * @param searchResult The list of bus stops that was found
 */
data class BusArrivalUIState(
    val busArrivalData: List<Pair<Pair<BusStopInfo?, BusStopInfo?>, BusService>>? = null,
    val busStopInfo: BusStopInfo? = null,
    val busRoutes: List<BusRouteInfo> = listOf(),
    val isRefreshing: Boolean = false,
    val showBusArrival: Boolean = true,
    val showBusType: Boolean = true,
    val showFirstLastBus: Boolean = false,
    val busStopCodeInput: String = "",
    val expanded: Boolean = false,
    val connectionIssue: Boolean = false,
    val searchResult: List<BusStopInfo> = listOf()
)
