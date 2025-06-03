package com.aiepoissac.busapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.LocationManager
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.LatLong
import com.aiepoissac.busapp.data.businfo.findBusServiceTo
import com.aiepoissac.busapp.data.businfo.truncateTillBusStop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusToMRTStationsViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val latitude: Double = 1.290270,
    private val longitude: Double = 103.851959,
    private val stationCode: String,
    private val distanceThreshold: Int = 1500
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusToMRTStationsViewModel::class.java)) {
            BusToMRTStationsViewModel(
                busRepository = busRepository,
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
    start: LatLong,
    stationCode: String,
    distanceThreshold: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusesToMRTStationUIState(
        distanceThreshold = distanceThreshold))
    val uiState: StateFlow<BusesToMRTStationUIState> = _uiState.asStateFlow()

    init {
        LocationManager.stopFetchingLocation()
        viewModelScope.launch {
            val mrtStation = busRepository.getMRTStation(stationCode)

            _uiState.update {
                it.copy(
                    routes = findBusServiceTo(
                        origin = start,
                        target = mrtStation,
                        distanceThreshold = distanceThreshold,
                        busRepository = busRepository
                    ),
                    mrtStation = mrtStation)
            }
        }
    }

    fun sortByNumberOfStops() {
        _uiState.update {
            it.copy(
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
        _uiState.update {
            it.copy(
                routes = uiState.value.routes
                    .sortedBy { it.first.first },
                sortedByNumberOfStops = false,
                sortedByBusStop = true,
                sortedByWalkingDistance = false
            )
        }
    }

    fun sortByWalkingDistance() {
        _uiState.update {
            it.copy(
                routes = uiState.value.routes
                    .sortedBy { it.first.first + it.second.first },
                sortedByNumberOfStops = false,
                sortedByBusStop = false,
                sortedByWalkingDistance = true
            )
        }
    }
}