package com.aiepoissac.busapp.ui

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.GoogleMapsURLGenerator
import com.aiepoissac.busapp.LocationManager
import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.LatLong
import com.aiepoissac.busapp.data.businfo.findNearbyBusStops
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.aiepoissac.busapp.data.bicycle.BicycleParkingGetter
import com.aiepoissac.busapp.data.businfo.MRTStation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import java.io.IOException

class NearbyViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val locationManager: LocationManager = BusApplication.instance.container.locationManager,
    private val bicycleParkingGetter: BicycleParkingGetter = BusApplication.instance.container.bicycleParkingGetter,
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
                bicycleParkingGetter = bicycleParkingGetter,
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
    private val bicycleParkingGetter: BicycleParkingGetter,
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
                locationManager.startFetchingLocation(fastRefresh = false)
                _uiState.update { it.copy(isLiveLocation = true) }

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

    fun setShowNearbyBusStopsOnMap(showNearbyBusStopsOnMap: Boolean) {
        _uiState.update {
            it.copy(showNearbyBusStopsOnMap = showNearbyBusStopsOnMap)
        }
    }

    fun setShowBicycleParkingOnMap(showBicycleParkingOnMap: Boolean) {
        _uiState.update {
            it.copy(showBicycleParkingOnMap = showBicycleParkingOnMap)
        }
        if (showBicycleParkingOnMap) {
            refreshBicycleParkingList()
        }
    }

    fun updateLiveLocation() {
        viewModelScope.launch {
            if (uiState.value.isLiveLocation) {
                locationManager.startFetchingLocation(fastRefresh = false)
            }
            locationManager.currentLocation
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { location ->
                    if (uiState.value.isLiveLocation) {
                        updateLocation(LatLong(location.latitude, location.longitude))
                        stopLiveLocation()
                    }
                }
        }
    }

    private suspend fun updateLocation(point: LatLong) {

        val busStopList = findNearbyBusStops(
            point = point,
            distanceThreshold = uiState.value.distanceThreshold,
            busRepository = busRepository,
            limit = uiState.value.busStopListLimit
        )
            .map { Pair(
                first = it.first,
                second = busRepository.getBusStopWithBusRoutes(it.second.busStopCode))
            }

        val mrtStationList = busRepository
            .getAllMRTStations()
            .map { Pair(it.distanceFromInMetres(point), it) }
            .sortedBy { it.first }

        _uiState.update { nearbyUIState ->
            nearbyUIState.copy(
                busStopList = busStopList,
                mrtStationList = mrtStationList,
                point = point
            )
        }

        if (uiState.value.showBicycleParkingOnMap) {
            refreshBicycleParkingList()
        }

        val target = LatLng(point.latitude, point.longitude)

        _markerState.update {
            MarkerState(target)
        }

        updateCameraPosition(target)
    }

    private fun refreshBicycleParkingList() {
        viewModelScope.launch {
            try {
                val bicycleParkingList = bicycleParkingGetter
                    .getBicycleParking(uiState.value.point)
                    .map { Pair(it.distanceFromInMetres(uiState.value.point), it) }
                    .sortedBy { it.first }

                _uiState.update { nearbyUIState ->
                    nearbyUIState.copy(bicycleParkingList = bicycleParkingList)
                }
            } catch(e: IOException) {
                _uiState.update {
                    it.copy(
                        bicycleParkingList = null,
                        showBicycleParkingOnMap = false
                    )
                }
            }
        }
    }

    fun updateLocation(latLng: LatLng) {
        viewModelScope.launch {
            updateLocation(LatLong(latLng.latitude, latLng.longitude))
            stopLiveLocation()
        }
    }

    private fun updateCameraPosition(target: LatLng, zoomIn: Boolean = false) {
        _cameraPositionState.update {
            CameraPositionState(
                position = CameraPosition(
                    target,
                    if (zoomIn) 17f else it.position.zoom,
                    it.position.tilt,
                    it.position.bearing
                )
            )
        }
    }

    fun recenterCamera() {
        setCameraPositionToLocation(uiState.value.point)
    }

    fun setCameraPositionToLocation(point: HasCoordinates, zoomIn: Boolean = false) {
        _uiState.update {
            it.copy(
                showNearbyBusStops = false,
                showNearbyMRTStations = false
            )
        }
        stopLiveLocation()
        val target = LatLng(point.getCoordinates().first, point.getCoordinates().second)
        updateCameraPosition(target = target, zoomIn = zoomIn)
    }

    fun openDirections(destination: HasCoordinates, travelMode: GoogleMapsURLGenerator.TravelMode) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            GoogleMapsURLGenerator.directions(
                origin = uiState.value.point,
                destination = destination,
                travelMode = travelMode
            ).toUri()
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