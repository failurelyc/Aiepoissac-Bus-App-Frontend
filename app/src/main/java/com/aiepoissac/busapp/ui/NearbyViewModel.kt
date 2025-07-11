package com.aiepoissac.busapp.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.GoogleMapsURLGenerator
import com.aiepoissac.busapp.LocationManager
import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.LatLong
import com.aiepoissac.busapp.data.businfo.findNearbyBusStops
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
import androidx.core.net.toUri
import com.aiepoissac.busapp.data.businfo.MRTStation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState

class NearbyViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val locationManager: LocationManager = BusApplication.instance.container.locationManager,
    private val latitude: Double = 1.290270,
    private val longitude: Double = 103.851959,
    private val isLiveLocation: Boolean = false,
    private val distanceThreshold: Int = 5000,
    private val busStopListLimit: Int = 50
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NearbyViewModel::class.java)) {
            NearbyViewModel(
                busRepository = busRepository,
                locationManager = locationManager,
                point = LatLong(latitude, longitude),
                distanceThreshold = distanceThreshold,
                busStopListLimit = busStopListLimit,
                isLiveLocation = isLiveLocation
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class NearbyViewModel(
    private val busRepository: BusRepository,
    private val locationManager: LocationManager,
    point: LatLong,
    isLiveLocation: Boolean,
    distanceThreshold: Int,
    busStopListLimit: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NearbyUIState(
            distanceThreshold = distanceThreshold,
            busStopListLimit = busStopListLimit,
            point = point,
            isLiveLocation = isLiveLocation
        )
    )
    val uiState: StateFlow<NearbyUIState> = _uiState.asStateFlow()

    private val _cameraPositionState = MutableStateFlow(
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(LatLng(point.latitude, point.longitude), 15f)
        )
    )
    val cameraPositionState = _cameraPositionState.asStateFlow()

    private val _markerState = MutableStateFlow(
        MarkerState(position = LatLng(point.latitude, point.longitude))
    )

    val markerState = _markerState.asStateFlow()

    private var lastTimeToggleLocationPressed: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    init {
        viewModelScope.launch {
            if (!isLiveLocation) {
                locationManager.stopFetchingLocation()
                updateLocation(point)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopFetchingLocation()
    }

    fun toggleFreezeLocation() {

        if (uiState.value.isLiveLocation) {
            stopLiveLocation()
        } else {
            val threshold = 2
            val currentTime = LocalDateTime.now()
            val difference = Duration.between(lastTimeToggleLocationPressed, currentTime).seconds
            if (difference > threshold) {
                locationManager.startFetchingLocation(fastRefresh = false)
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

    fun stopLiveLocation() {
        locationManager.stopFetchingLocation()
        _uiState.update { it.copy(isLiveLocation = false) }
    }

    fun toggleShowNearbyBusStops() {
        _uiState.update {
            it.copy(showNearbyBusStops = !uiState.value.showNearbyBusStops)
        }
    }

    fun toggleShowNearbyMRTStations() {
        _uiState.update {
            it.copy(showNearbyMRTStations = !uiState.value.showNearbyMRTStations)
        }
    }

    fun updateLiveLocation() {
        viewModelScope.launch {
            if (uiState.value.isLiveLocation) {
                locationManager.startFetchingLocation(fastRefresh = false)
            }
            snapshotFlow { locationManager.currentLocation.value }
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { location ->
                    if (uiState.value.isLiveLocation) {
                        withContext(Dispatchers.Main) {
                            lastTimeToggleLocationPressed = LocalDateTime.now()
                        }
                        updateLocation(LatLong(location.latitude, location.longitude))
                        stopLiveLocation()
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
                    busRepository = busRepository,
                    limit = uiState.value.busStopListLimit
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

        val target = LatLng(point.latitude, point.longitude)

        _markerState.update {
            MarkerState(target)
        }

        updateCameraPosition(target)
    }

    fun updateLocation(latLng: LatLng) {
        viewModelScope.launch {
            updateLocation(LatLong(latLng.latitude, latLng.longitude))
            stopLiveLocation()
        }
    }

    fun updateCameraPosition(target: LatLng, zoomIn: Boolean = false) {
        _cameraPositionState.update {
            CameraPositionState(
                position = CameraPosition(
                    target,
                    if (zoomIn) 25f else it.position.zoom,
                    it.position.tilt,
                    it.position.bearing
                )
            )
        }
    }

    fun setCameraPositionToLocation(point: HasCoordinates) {
        _uiState.update {
            it.copy(
                showNearbyBusStops = false,
                showNearbyMRTStations = false
            )
        }
        stopLiveLocation()
        val target = LatLng(point.getCoordinates().first, point.getCoordinates().second)
        updateCameraPosition(target = target, zoomIn = true)
    }

    fun openDirections(destination: HasCoordinates) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            GoogleMapsURLGenerator.directions(uiState.value.point, destination).toUri()
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(BusApplication.instance, intent, null)
    }

    fun openDirectionsToMRTStation(destination: MRTStation) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            GoogleMapsURLGenerator.directionsToMRTStation(uiState.value.point, destination).toUri()
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(BusApplication.instance, intent, null)
    }
}