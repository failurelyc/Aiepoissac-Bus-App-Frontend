package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.bicycle.BicycleParking
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * This class stores the UI state for the Nearby Bus Stops page.
 *
 * @param originalBusStopList A list of all bus stops within the distance threshold
 * @param busStopList A list of all bus stops with bus route information
 * within the distance threshold and limited by bus stop list limit.
 * @param mrtStationList A list of all MRT stations
 * @param bicycleParkingList A list of all bicycle parking list within 500m
 * @param point The centre of the circle
 * @param distanceThreshold The maximum distance between a nearby bus stop and the centre of the circle
 * @param busStopListLimit The maximum size of the displayed bus stop list
 * @param isLiveLocation Whether live user location should be fetched
 * @param showNearbyBusStops Whether the nearby bus stops list is shown
 * @param showNearbyMRTStations Whether the nearby MRT stations list is shown
 * @param showNearbyBusStopsOnMap Whether nearby bus stops are shown on the map
 * @param showBicycleParkingOnMap Whether nearby bicycle parking locations are shown on the map
 * @param currentTime The time input
 * @param dayOfWeek The day of week input
 * @param showOnlyOperatingBusServices Whether bus services that are not operating
 *  * at a certain time and day of week are removed
 * @param showTimeDial Whether the time input dialog is shown
 * @param searchingForBusStops Whether the app is searching for nearby bus stops
 */
data class NearbyUIState(
    val originalBusStopList: List<Pair<Int, BusStopInfo>> = listOf(),
    val busStopList: List<Pair<Int, BusStopInfoWithBusRoutesInfo>> = listOf(),
    val mrtStationList: List<Pair<Int, MRTStation>> = listOf(),
    val bicycleParkingList: List<Pair<Int, BicycleParking>>? = listOf(),
    val point: HasCoordinates,
    val distanceThreshold: Int,
    val busStopListLimit: Int,
    val isLiveLocation: Boolean,
    val showNearbyBusStops: Boolean = false,
    val showNearbyMRTStations: Boolean = false,
    val showNearbyBusStopsOnMap: Boolean = true,
    val showBicycleParkingOnMap: Boolean = false,
    val currentTime: LocalTime = LocalTime.now(),
    val dayOfWeek: DayOfWeek = DayOfWeek.FRIDAY,
    val showOnlyOperatingBusServices: Boolean = false,
    val showTimeDial: Boolean = false,
    val searchingForBusStops: Boolean = false
)
