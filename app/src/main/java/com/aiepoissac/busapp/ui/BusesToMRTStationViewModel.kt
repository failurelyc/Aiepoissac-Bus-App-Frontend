package com.aiepoissac.busapp.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.LatLong
import com.aiepoissac.busapp.data.businfo.findBusServiceTo
import com.aiepoissac.busapp.userdata.JourneyInfo
import com.aiepoissac.busapp.userdata.JourneySegmentInfo
import com.aiepoissac.busapp.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.min

/**
 * This class is the View Model factory for the View Model
 * for Bus Service to other MRT stations page
 *
 * @param busRepository The repository of the bus data
 * @param userDataRepository The repository of the user data
 * @param latitude The latitude of the origin point
 * @param longitude The longitude of the origin point
 * @param stationCode The station code of the destination MRT station
 * @param distanceThreshold The maximum distance between the origin and the origin bus stop
 * added with the distance between the destination bus stop and the MRT station
 */
class BusToMRTStationsViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val userDataRepository: UserDataRepository = BusApplication.instance.container.userDataRepository,
    private val latitude: Double = 1.290270,
    private val longitude: Double = 103.851959,
    private val stationCode: String,
    private val distanceThreshold: Int = 1600
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusToMRTStationsViewModel::class.java)) {
            BusToMRTStationsViewModel(
                busRepository = busRepository,
                userDataRepository = userDataRepository,
                start = LatLong(latitude, longitude),
                stationCode = stationCode,
                distanceThreshold = distanceThreshold,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

/**
 * This class is the View Model factory for the View Model
 * for Bus Service to other MRT stations page
 *
 * @param busRepository The repository of the bus data
 * @param userDataRepository The repository of the user data
 * @param start The origin location
 * @param stationCode The station code of the destination MRT station
 * @param distanceThreshold The maximum distance between the origin and the origin bus stop
 * added with the distance between the destination bus stop and the MRT station
 */
class BusToMRTStationsViewModel (
    private val busRepository: BusRepository,
    private val userDataRepository: UserDataRepository,
    start: HasCoordinates,
    stationCode: String,
    distanceThreshold: Int
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            BusesToMRTStationUIState(
                distanceThreshold = distanceThreshold
            )
        )
    val uiState: StateFlow<BusesToMRTStationUIState> = _uiState.asStateFlow()

    /**
     * Loads the routes from the origin location to the destination MRT station.
     */
    init {
        viewModelScope.launch {
            val mrtStation = busRepository.getMRTStation(stationCode)
            val distance = mrtStation.distanceFromInMetres(start)

            val busRoutes = findBusServiceTo(
                origin = start,
                target = mrtStation,
                distanceThreshold = min(distanceThreshold, distance),
                busRepository = busRepository
            )

            _uiState.update {
                it.copy(
                    originalRoutes = busRoutes,
                    routes = busRoutes,
                    mrtStation =
                        Pair(
                            first = distance,
                            second = mrtStation
                        )
                )
            }

            sortByWalkingDistance()
        }
    }

    /**
     * Sort the route list displayed by number of stops of each route
     */
    fun sortByNumberOfStops() {
        _uiState.update { busesToMRTStationUIState ->
            busesToMRTStationUIState.copy(
                routes = uiState.value.routes
                    .sortedBy { it.second.second.busRouteInfo.stopSequence -
                            it.first.second.busRouteInfo.stopSequence },
                sortedByNumberOfStops = true,
                sortedByBusStop = false,
                sortedByWalkingDistance = false
            )
        }
    }

    /**
     * Sort the route list displayed by the origin bus stop of each route
     */
    fun sortByOriginBusStop() {
        _uiState.update { busesToMRTStationUIState ->
            busesToMRTStationUIState.copy(
                routes = uiState.value.routes
                    .sortedBy { it.first.first },
                sortedByNumberOfStops = false,
                sortedByBusStop = true,
                sortedByWalkingDistance = false
            )
        }
    }

    /**
     * Sort the route list displayed by the distance between the origin and the origin bus stop
     * added with the distance between the destination bus stop and the MRT station
     */
    fun sortByWalkingDistance() {
        _uiState.update { busesToMRTStationUIState ->
            busesToMRTStationUIState.copy(
                routes = uiState.value.routes
                    .sortedBy { it.first.first + it.second.first },
                sortedByNumberOfStops = false,
                sortedByBusStop = false,
                sortedByWalkingDistance = true
            )
        }
    }

    /**
     * Remove bus services that are not operating at a certain time and day of week.
     *
     * @param showOnlyOperatingBusServices True if bus services not operating should be removed.
     */
    fun setShowOnlyOperatingBusServices(showOnlyOperatingBusServices: Boolean) {
        _uiState.update {
            it.copy(
                showOnlyOperatingBusServices = showOnlyOperatingBusServices
            )
        }
        updateBusRoutesList()
    }

    /**
     * Sets whether the time input dialog should be displayed.
     *
     * @param showTimeDial True if the time input dialog should be displayed.
     */
    fun setShowTimeDial(showTimeDial: Boolean) {
        _uiState.update {
            it.copy(
                showTimeDial = showTimeDial
            )
        }
    }

    /**
     * Update the time to the time input and update the bus routes list to only show
     * bus services operating at the updated time and day of week.
     *
     * @param timePickerState The time input
     */
    @OptIn(ExperimentalMaterial3Api::class)
    fun updateTime(timePickerState: TimePickerState) {
        _uiState.update {
            it.copy(
                currentTime = LocalTime.of(timePickerState.hour, timePickerState.minute),
                showTimeDial = false
            )
        }
        updateBusRoutesList()
    }

    /**
     * Update the day of week and update the bus routes list to only show
     * bus services operating at the updated day of week and time.
     */
    fun setDayOfWeek() {
        if (!uiState.value.searchingForBusServices) {
            val dayOfWeek =
                when (uiState.value.dayOfWeek) {
                    DayOfWeek.SATURDAY -> DayOfWeek.SUNDAY
                    DayOfWeek.SUNDAY -> DayOfWeek.MONDAY
                    else -> DayOfWeek.SATURDAY
                }

            _uiState.update {
                it.copy(dayOfWeek = dayOfWeek)
            }
            updateBusRoutesList()
        }

    }

    /**
     * Update the bus routes list to show the full list or the list that only contains
     * bus services operating at the updated day of week and time.
     */
    private fun updateBusRoutesList() {
        _uiState.update {
            it.copy(
                searchingForBusServices = true,
                sortedByNumberOfStops = false,
                sortedByBusStop = false,
                sortedByWalkingDistance = false
            )
        }
        viewModelScope.launch {
            if (uiState.value.showOnlyOperatingBusServices) {
                val routes = uiState.value.originalRoutes
                    .filter {
                        val origin = it.first.second.busRouteInfo
                        val destination = it.second.second.busRouteInfo
                        val dayOfWeek = uiState.value.dayOfWeek
                        val time = uiState.value.currentTime
                        when (dayOfWeek) {
                            DayOfWeek.SATURDAY -> origin.isOperatingOnSaturday(time)
                            DayOfWeek.SUNDAY -> origin.isOperatingOnSunday(time)
                            else -> origin.isOperatingOnWeekday(time)
                        }
                    }
                _uiState.update {
                    it.copy(
                        searchingForBusServices = false,
                        routes = routes
                    )
                }

            } else {
                _uiState.update {
                    it.copy(
                        searchingForBusServices = false,
                        routes = uiState.value.originalRoutes
                    )
                }
            }

            sortByWalkingDistance()
        }

    }

    /**
     * Toggles whether Add Saved Journey dialog should be shown.
     */
    fun toggleShowAddSavedJourneyDialog() {
        _uiState.update {
            it.copy(
                showAddSavedJourneyDialog = !uiState.value.showAddSavedJourneyDialog
            )
        }
    }

    /**
     * Updates the description input field for Add Saved Journey dialog.
     *
     * @param description The updated description
     */
    fun updateDescriptionInput(description: String) {
        _uiState.update {
            it.copy(descriptionInput = description)
        }
    }

    /**
     * Add the list of bus routes to a new saved journey with the description in the
     * description input field, and close the Add Saved Journey Dialog.
     */
    fun addSavedJourney() {
        viewModelScope.launch {
            val journeyID = generateRandomString(length = 8)
            userDataRepository.insertJourney(
                JourneyInfo(
                    journeyID = journeyID,
                    description = uiState.value.descriptionInput
                )
            )
            var count = 0
            uiState.value.routes
                .take(n = 10)
                .forEach {
                    val origin = it.first.second.busRouteInfo
                    val destination = it.second.second.busRouteInfo
                    val routeLength = busRepository.getBusServiceRouteLength(origin.serviceNo, origin.direction)
                    val journeySegment =
                        JourneySegmentInfo(
                            journeyID = journeyID,
                            sequence = count,
                            serviceNo = origin.serviceNo,
                            direction = origin.direction,
                            originBusStopSequence = origin.stopSequence % routeLength,
                            destinationBusStopSequence = destination.stopSequence % routeLength
                        )
                    userDataRepository.insertJourneySegment(journeySegment)
                    count++
                }
            _uiState.update {
                it.copy(
                    showAddSavedJourneyDialog = false,
                    descriptionInput = ""
                )
            }
        }
    }

}