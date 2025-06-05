package com.aiepoissac.busapp.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.LocationManager
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.LatLong
import com.aiepoissac.busapp.data.businfo.findNearbyBusStops
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime

class NearbyViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val latitude: Double = 1.290270,
    private val longitude: Double = 103.851959,
    private val isLiveLocation: Boolean = false,
    private val distanceThreshold: Int = 1500
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NearbyViewModel::class.java)) {
            NearbyViewModel(
                busRepository = busRepository,
                point = LatLong(latitude, longitude),
                distanceThreshold = distanceThreshold,
                isLiveLocation = isLiveLocation
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class NearbyViewModel(
    private val busRepository: BusRepository,
    point: LatLong,
    isLiveLocation: Boolean,
    distanceThreshold: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NearbyUIState(
            distanceThreshold = distanceThreshold,
            point = point,
            isLiveLocation = isLiveLocation
        )
    )
    val uiState: StateFlow<NearbyUIState> = _uiState.asStateFlow()

    private var lastTimeToggleLocationPressed: LocalDateTime by mutableStateOf(LocalDateTime.now())

    init {
        viewModelScope.launch {
            if (!isLiveLocation) {
                LocationManager.stopFetchingLocation()
                updateLocation(point)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocationManager.stopFetchingLocation()
    }

    fun toggleFreezeLocation() {

        if (uiState.value.isLiveLocation) {
            LocationManager.stopFetchingLocation()
            _uiState.update { it.copy(isLiveLocation = false) }
        } else {
            val threshold = 10
            val currentTime = LocalDateTime.now()
            val difference = Duration.between(lastTimeToggleLocationPressed, currentTime).seconds
            if (difference > threshold) {
                LocationManager.startFetchingLocation()
                _uiState.update { it.copy(isLiveLocation = true) }
            } else {
                Toast.makeText(
                    BusApplication.instance,
                    "Try again in ${threshold - difference}s",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun updateLiveLocation() {
        viewModelScope.launch {
            if (uiState.value.isLiveLocation) {
                LocationManager.startFetchingLocation()
            }
            snapshotFlow { LocationManager.currentLocation.value }
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { location ->
                    if (uiState.value.isLiveLocation) {
                        withContext(Dispatchers.Main) {
                            lastTimeToggleLocationPressed = LocalDateTime.now()
                        }
                        updateLocation(LatLong(location.latitude, location.longitude))
                    }
                }
        }
    }

    private suspend fun updateLocation(point: LatLong) {

        _uiState.update { nearbyUiState ->
            nearbyUiState.copy(
                busStopList = findNearbyBusStops(
                    point = point,
                    distanceThreshold = uiState.value.distanceThreshold,
                    busRepository = busRepository
                )
                    .map { Pair(
                        first = it.first,
                        second = busRepository.getBusStopWithBusRoutes(it.second.busStopCode))
                    },
                mrtStationList = busRepository
                    .getAllMRTStations()
                    .map { Pair(it.distanceFromInMetres(point), it) }
                    .sortedBy { it.first },
                point = point
            )
        }

    }

}