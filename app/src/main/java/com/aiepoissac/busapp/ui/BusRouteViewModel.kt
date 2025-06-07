package com.aiepoissac.busapp.ui

import android.location.Location
import android.util.Log
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
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.LatLong
import com.aiepoissac.busapp.data.businfo.attachDistanceFromPoint
import com.aiepoissac.busapp.data.businfo.isLoop
import com.aiepoissac.busapp.data.businfo.truncateLoopRoute
import com.aiepoissac.busapp.data.businfo.truncateTillBusStop
import kotlinx.coroutines.Dispatchers
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

class BusRouteViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val serviceNo: String,
    private val direction: Int,
    private val stopSequence: Int
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusRouteViewModel::class.java)) {
            BusRouteViewModel(
                busRepository = busRepository,
                serviceNo = serviceNo,
                direction = direction,
                stopSequence = stopSequence
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class BusRouteViewModel(
    private val busRepository: BusRepository,
    serviceNo: String,
    direction: Int,
    stopSequence: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRouteUIState())
    val uiState: StateFlow<BusRouteUIState> = _uiState.asStateFlow()

    init {
        updateBusService(serviceNo, direction, stopSequence)
        Log.d(
            "BusServiceViewModel",
            "BusServiceViewModel created with parameters: $serviceNo, $direction")
    }

    override fun onCleared() {
        super.onCleared()
        LocationManager.stopFetchingLocation()
    }

    private fun updateBusService(serviceNo: String, direction: Int, stopSequence: Int = -1) {
        viewModelScope.launch {

            val busRoute: List<BusRouteInfoWithBusStopInfo> = busRepository
                .getBusServiceRoute(serviceNo = serviceNo, direction = direction)

            _uiState.update {
                it.copy(
                    busRoute = attachDistanceFromCurrentLocation(busRoute),
                    busStopSequenceOffset = 0,
                    originalBusRoute = busRoute,
                    busServiceInfo = busRepository
                        .getBusService(serviceNo = serviceNo, direction = direction),
                    truncated = false,
                    truncatedAfterLoopingPoint = false
                )
            }
            if (stopSequence >= 0) {
                setFirstBusStop(stopSequence)
            }
        }
    }

    fun toggleDirection() {
        val busServiceInfo = uiState.value.busServiceInfo
        if (!isLoop(uiState.value.originalBusRoute) && busServiceInfo != null) {
            updateBusService(
                serviceNo = busServiceInfo.serviceNo,
                direction = if (busServiceInfo.direction == 1) 2 else 1
            )
        }
    }

    fun setFirstBusStop(stopSequence: Int) {
        viewModelScope.launch {
            if (uiState.value.busRoute.size > 1) {
                val truncatedRoute = truncateTillBusStop(
                    route = uiState.value.originalBusRoute,
                    stopSequence = stopSequence + uiState.value.busStopSequenceOffset
                )
                _uiState.update {
                    it.copy(
                        busRoute = attachDistanceFromCurrentLocation(truncatedRoute),
                        truncated = true,
                        busStopSequenceOffset = (stopSequence + uiState.value.busStopSequenceOffset)
                                % (uiState.value.originalBusRoute.size - 1))
                }
            } else {
                setOriginalFirstBusStop()
            }
        }
    }

    fun setLoopingPointAsFirstBusStop() {
        _uiState.update {
            val truncatedRoute = truncateLoopRoute(
                route = uiState.value.originalBusRoute,
                after = true
            )
            it.copy(
                busRoute = attachDistanceFromCurrentLocation(truncatedRoute.second),
                truncated = false,
                truncatedAfterLoopingPoint = true,
                busStopSequenceOffset = truncatedRoute.first
            )
        }
    }

    fun setOriginalFirstBusStop() {
        val busRoute = uiState.value.originalBusRoute
        _uiState.update {
            it.copy(
                busRoute = attachDistanceFromCurrentLocation(busRoute),
                truncated = false,
                truncatedAfterLoopingPoint = false,
                busStopSequenceOffset = 0
            )
        }
    }

    fun toggleShowFirstLastBusToTrue() {
        val showFirstLastBus = uiState.value.showFirstLastBus
        _uiState.update {
            it.copy(showFirstLastBus = !showFirstLastBus)
        }
    }

    fun toggleFreezeLocation() {

        if (uiState.value.isLiveLocation) {
            LocationManager.stopFetchingLocation()
            _uiState.update { it.copy(isLiveLocation = false) }
        } else {
            val threshold = LocationManager.REFRESH_INTERVAL_IN_SECONDS
            val currentTime = LocalDateTime.now()
            val difference = Duration.between(uiState.value.lastTimeLocationUpdated, currentTime).seconds
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
                        val timeNow = LocalDateTime.now()
                        updateLocation(
                            location = location,
                            time = timeNow
                        )
                    }
                }
        }
    }

    private fun updateLocation(location: Location, time: LocalDateTime) {
        _uiState.update {
            it.copy(
                busRoute = uiState.value.busRoute
                    .map { Pair(
                        first = it.second.busStopInfo
                            .distanceFromInMetres(LatLong(location.latitude, location.longitude)),
                        second = it.second
                    ) },
                lastTimeLocationUpdated = time,
                currentSpeed = if (location.hasSpeed()) (location.speed * 3.6).toInt() else 0
            )
        }
    }

    private fun attachDistanceFromCurrentLocation(route: List<BusRouteInfoWithBusStopInfo>)
    : List<Pair<Int, BusRouteInfoWithBusStopInfo>> {
        val location = LocationManager.currentLocation.value
        if (location != null) {
            return attachDistanceFromPoint(LatLong(location.latitude, location.longitude), route)
        } else {
            return route.map { Pair(0, it) }
        }
    }



}