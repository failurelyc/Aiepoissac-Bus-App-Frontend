package com.aiepoissac.busapp.ui

import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.GoogleMapsURLGenerator
import com.aiepoissac.busapp.LocationRepository
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
import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * This class is the View Model factory for the View Model for the Nearby Bus Stops Information page
 *
 * @param busRepository The repository of the bus data
 * @param locationRepository The source of the user location
 * @param bicycleParkingGetter The source of bicycle parking locations
 * @param latitude The latitude of the centre of the circle
 * @param longitude The longitude of the centre of the circle
 * @param isLiveLocation Whether live user location should be fetched
 * @param distanceThreshold The radius of the circle
 * @param busStopListLimit The maximum number of the closest bus stops to show in the route list and map
 */
class NearbyViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val locationRepository: LocationRepository = BusApplication.instance.container.locationRepository,
    private val bicycleParkingGetter: BicycleParkingGetter = BusApplication.instance.container.bicycleParkingGetter,
    private val latitude: Double = 1.290270,
    private val longitude: Double = 103.851959,
    private val isLiveLocation: Boolean = false,
    private val distanceThreshold: Int = 10000,
    private val busStopListLimit: Int = 100
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NearbyViewModel::class.java)) {
            NearbyViewModel(
                busRepository = busRepository,
                locationRepository = locationRepository,
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

/**
 * This class is the View Model for the Nearby Bus Stops Information page
 *
 * @param busRepository The repository of the bus data
 * @param locationRepository The source of the user location
 * @param bicycleParkingGetter The source of bicycle parking locations
 * @param point The coordinates of the centre of the circle
 * @param isLiveLocation Whether live user location should be fetched
 * @param distanceThreshold The radius of the circle
 * @param busStopListLimit The maximum number of the closest bus stops to show in the route list and map
 */
class NearbyViewModel(
    private val busRepository: BusRepository,
    private val locationRepository: LocationRepository,
    private val bicycleParkingGetter: BicycleParkingGetter,
    point: HasCoordinates,
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
                locationRepository.stopFetchingLocation()
                updateLocation(point)
            }
        }
    }

    /**
     * Stop fetching of location when ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        locationRepository.stopFetchingLocation()
    }

    /**
     * Toggle whether user location should be fetched.
     */
    fun toggleFreezeLocation() {
        if (uiState.value.isLiveLocation) {
            stopLiveLocation()
        } else {
            locationRepository.startFetchingLocation(fastRefresh = false)
            _uiState.update { it.copy(isLiveLocation = true) }

        }
    }

    /**
     * Stop user location from being fetched.
     */
    fun stopLiveLocation() {
        locationRepository.stopFetchingLocation()
        _uiState.update { it.copy(isLiveLocation = false) }
    }

    /**
     * Toggle whether the nearby bus stops list is shown.
     */
    fun toggleShowNearbyBusStops() {
        _uiState.update {
            it.copy(showNearbyBusStops = !uiState.value.showNearbyBusStops)
        }
    }

    /**
     * Toggle whether the nearby MRT stations list is shown.
     */
    fun toggleShowNearbyMRTStations() {
        _uiState.update {
            it.copy(showNearbyMRTStations = !uiState.value.showNearbyMRTStations)
        }
    }

    /**
     * Set whether nearby bus stops are shown on the map.
     *
     * @param showNearbyBusStopsOnMap True if nearby bus stops should be shown
     */
    fun setShowNearbyBusStopsOnMap(showNearbyBusStopsOnMap: Boolean) {
        _uiState.update {
            it.copy(showNearbyBusStopsOnMap = showNearbyBusStopsOnMap)
        }
    }

    /**
     * Set whether nearby bicycle parking locations are shown on the map.
     * Also refresh the bicycle parking locations if set to true.
     *
     * @param showBicycleParkingOnMap True if nearby bicycle parking locations should be shown
     */
    fun setShowBicycleParkingOnMap(showBicycleParkingOnMap: Boolean) {
        _uiState.update {
            it.copy(showBicycleParkingOnMap = showBicycleParkingOnMap)
        }
        if (showBicycleParkingOnMap) {
            viewModelScope.launch {
                refreshBicycleParkingList()
            }
        }
    }

    /**
     * Listen for updates to user location. This method should only be called once.
     * Once the user location is updated, user location fetching is stopped.
     */
    fun updateLiveLocation() {
        viewModelScope.launch {
            if (uiState.value.isLiveLocation) {
                locationRepository.startFetchingLocation(fastRefresh = false)
            }
            locationRepository.currentLocation
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

    /**
     * Update the centre of the circle and the marker on the map to the point specified.
     * Also update the nearby bus stops and nearby MRT station lists.
     * Also update the nearby bicycle parking location list if shown on map.
     *
     * @param point The new centre of the circle
     */
    private suspend fun updateLocation(point: HasCoordinates) {

        _uiState.update { nearbyUIState ->
            nearbyUIState.copy(
                point = point
            )
        }

        updateOriginalBusStopList()

        val mrtStationList = busRepository
            .getAllMRTStations()
            .map { Pair(it.distanceFromInMetres(point), it) }
            .sortedBy { it.first }

        _uiState.update { nearbyUIState ->
            nearbyUIState.copy(
                mrtStationList = mrtStationList
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

    /**
     * Updates the original bus stop list to contain all bus stops within the distance
     * threshold of the centre of the circle.
     */
    private fun updateOriginalBusStopList() {
        viewModelScope.launch {
            val busStopList = findNearbyBusStops(
                point = uiState.value.point,
                distanceThreshold = uiState.value.distanceThreshold,
                busRepository = busRepository
            )

            _uiState.update { nearbyUIState ->
                nearbyUIState.copy(
                    originalBusStopList = busStopList
                )
            }

            updateBusStopList()

        }
    }

    /**
     * Updates the displayed bus stop with bus services list to contain the first closest bus stops
     * within the distance threshold of the centre of the circle, up to the list size limit.
     * If show only operating bus services is true, bus services not operating are removed and
     * all bus stops with no operating bus services are replaced by the next nearest bus stop
     * within the distance threshold but not in the list.
     */
    private fun updateBusStopList() {
        _uiState.update {
            it.copy(
                searchingForBusStops = true
            )
        }
        viewModelScope.launch {
            if (uiState.value.showOnlyOperatingBusServices) {
                val time = uiState.value.currentTime
                val busStopList = uiState.value.originalBusStopList
                    .asFlow()
                    .map {
                        val busStopWithRoutes = busRepository.getBusStopWithBusRoutes(it.second.busStopCode)
                        Pair(
                            first = it.first,
                            second = BusStopInfoWithBusRoutesInfo(
                                busStopInfo = busStopWithRoutes.busStopInfo,
                                busRoutesInfo = busStopWithRoutes.busRoutesInfo.filter { busRouteInfo ->
                                    when (uiState.value.dayOfWeek) {
                                        DayOfWeek.SUNDAY -> busRouteInfo.isOperatingOnSunday(time)
                                        DayOfWeek.SATURDAY -> busRouteInfo.isOperatingOnSaturday(time)
                                        else -> busRouteInfo.isOperatingOnWeekday(time)
                                    }
                                }
                            )
                        )
                    }
                    .filter {
                        it.second.busRoutesInfo.isNotEmpty()
                    }
                    .take(uiState.value.busStopListLimit)
                    .toList()

                _uiState.update {
                    it.copy(
                        busStopList = busStopList,
                        searchingForBusStops = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        busStopList = uiState.value.originalBusStopList
                            .take(n = 100)
                            .map {
                                Pair(
                                    first = it.first,
                                    second = busRepository.getBusStopWithBusRoutes(it.second.busStopCode)
                                )
                            },
                        searchingForBusStops = false
                    )
                }
            }
        }
    }

    /**
     * Refresh the bicycle parking list. If an exception occurs, show bicycle parking is turned off.
     */
    private suspend fun refreshBicycleParkingList() {
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

    /**
     * Update the centre of the circle to the selected location on the map.
     *
     * @param latLng The selected location on the map
     */
    fun updateLocation(latLng: LatLng) {
        viewModelScope.launch {
            updateLocation(LatLong(latLng.latitude, latLng.longitude))
            stopLiveLocation()
        }
    }

    /**
     * Update the camera of the map to the specified location.
     *
     * @param target The target location
     * @param zoomIn True if the map should be zoomed in, false otherwise
     */
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

    /**
     * Update the camera of the map to the centre of the circle.
     */
    fun recenterCamera() {
        setCameraPositionToLocation(uiState.value.point)
    }

    /**
     * Update the camera of the map to a specified location.
     *
     * @param point The target location
     * @param zoomIn True if the map should be zoomed in, false otherwise
     */
    fun setCameraPositionToLocation(point: HasCoordinates, zoomIn: Boolean = true) {
        stopLiveLocation()
        val target = LatLng(point.getCoordinates().first, point.getCoordinates().second)
        updateCameraPosition(target = target, zoomIn = zoomIn)
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
        updateBusStopList()
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
        updateBusStopList()
    }

    /**
     * Update the day of week and update the bus routes list to only show
     * bus services operating at the updated day of week and time.
     */
    fun setDayOfWeek() {
        if (!uiState.value.searchingForBusStops) {
            val dayOfWeek =
                when (uiState.value.dayOfWeek) {
                    DayOfWeek.SATURDAY -> DayOfWeek.SUNDAY
                    DayOfWeek.SUNDAY -> DayOfWeek.MONDAY
                    else -> DayOfWeek.SATURDAY
                }

            _uiState.update {
                it.copy(dayOfWeek = dayOfWeek)
            }
            updateBusStopList()
        }

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