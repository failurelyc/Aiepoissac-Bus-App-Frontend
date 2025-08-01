package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * This class stores the UI state for the Bus Service to other MRT stations page.
 *
 * @param originalRoutes The list of all routes that serves between the origin location and the MRT station.
 * @param routes The list of routes displayed to the user. This is a subset of originalRoutes.
 * @param distanceThreshold The maximum distance between the origin and the origin bus stop
 * added with the distance between the destination bus stop and the MRT station
 * @param mrtStation The destination MRT station
 * @param sortedByBusStop Whether routes is sorted by origin bus stop
 * @param sortedByNumberOfStops Whether routes is sorted by the number of bus stops of each route
 * @param sortedByWalkingDistance Whether routes is sorted by the distance between the origin and the origin bus stop
 * added with the distance between the destination bus stop and the MRT station
 * @param currentTime The time input
 * @param dayOfWeek The day of week input
 * @param showOnlyOperatingBusServices Whether bus services that are not operating
 * at a certain time and day of week are removed from routes
 * @param showTimeDial Whether the time input dialog is shown
 * @param searchingForBusServices Whether the app is searching for bus services
 * @param showAddSavedJourneyDialog Whether the Add Saved Journey Dialog is shown
 * @param descriptionInput The value of the description input in the Add Saved Journey Dialog
 */
data class BusesToMRTStationUIState (
    val originalRoutes: List<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> = listOf(),
    val routes: List<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> = listOf(),
    val distanceThreshold: Int,
    val mrtStation: Pair<Int, MRTStation>? = null,
    val sortedByBusStop: Boolean = false,
    val sortedByNumberOfStops: Boolean = false,
    val sortedByWalkingDistance: Boolean = false,
    val currentTime: LocalTime = LocalTime.now(),
    val dayOfWeek: DayOfWeek = DayOfWeek.FRIDAY,
    val showOnlyOperatingBusServices: Boolean = false,
    val showTimeDial: Boolean = false,
    val searchingForBusServices: Boolean = false,
    val showAddSavedJourneyDialog: Boolean = false,
    val descriptionInput: String = ""
)