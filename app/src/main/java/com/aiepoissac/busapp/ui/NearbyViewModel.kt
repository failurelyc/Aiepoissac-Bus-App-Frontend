package com.aiepoissac.busapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.LatLong
import com.aiepoissac.busapp.data.businfo.findNearbyBusStops
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NearbyViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val latitude: Double = 1.290270,
    private val longitude: Double = 103.851959,
    private val distanceThreshold: Int = 1500
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NearbyViewModel::class.java)) {
            NearbyViewModel(
                busRepository = busRepository,
                point = LatLong(latitude, longitude),
                distanceThreshold = distanceThreshold,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class NearbyViewModel(
    private val busRepository: BusRepository,
    point: LatLong,
    distanceThreshold: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NearbyUIState(
            distanceThreshold = distanceThreshold,
            point = point
        )
    )
    val uiState: StateFlow<NearbyUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { nearbyUiState ->
                nearbyUiState.copy(
                    busStopList = findNearbyBusStops(
                        point = point,
                        distanceThreshold = distanceThreshold,
                        busRepository = busRepository
                    )
                        .map { Pair(
                            first = it.first,
                            second = busRepository.getBusStopWithBusRoutes(it.second.busStopCode))
                        },
                    mrtStationList = busRepository
                        .getAllMRTStations()
                        .map { Pair(it.distanceFromInMetres(point), it) }
                        .sortedBy { it.first }
                )
            }
        }
    }

}