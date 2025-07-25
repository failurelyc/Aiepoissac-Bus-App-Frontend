package com.aiepoissac.busapp.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

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

class BusToMRTStationsViewModel (
    private val busRepository: BusRepository,
    private val userDataRepository: UserDataRepository,
    start: LatLong,
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

    fun setShowOnlyOperatingBusServices(showOnlyOperatingBusServices: Boolean) {
        _uiState.update {
            it.copy(
                showOnlyOperatingBusServices = showOnlyOperatingBusServices
            )
        }
        updateBusRoutesList()
    }

    fun setShowTimeDial(showTimeDial: Boolean) {
        _uiState.update {
            it.copy(
                showTimeDial = showTimeDial
            )
        }
    }

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

    fun toggleShowAddSavedJourneyDialog() {
        _uiState.update {
            it.copy(
                showAddSavedJourneyDialog = !uiState.value.showAddSavedJourneyDialog
            )
        }
    }

    fun updateDescriptionInput(description: String) {
        _uiState.update {
            it.copy(descriptionInput = description)
        }
    }

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