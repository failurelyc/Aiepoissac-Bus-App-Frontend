package com.aiepoissac.busapp.ui

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.LocationRepository
import com.aiepoissac.busapp.data.busarrival.BusArrivalGetter
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import com.aiepoissac.busapp.data.LatLong
import com.aiepoissac.busapp.data.businfo.attachDistanceFromPoint
import com.aiepoissac.busapp.data.businfo.isLoop
import com.aiepoissac.busapp.data.businfo.truncateLoopRoute
import com.aiepoissac.busapp.data.businfo.truncateTillBusStop
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime

/**
 * This class is the View Model factory for the View Model for the Bus Route Information page
 *
 * @param busRepository The repository of the bus data
 * @param busArrivalGetter The source of the bus arrival data
 * @param locationRepository The source of the user location
 * @param serviceNo The bus service number
 * @param direction The direction in which the bus travels (1 or 2), loop services only have 1 direction
 * @param stopSequence The bus stop sequence of the bus stop along the route
 * to be set as the 0th bus stop initially
 * @param showMap Whether the map should be shown initially
 * @param showLiveBuses Whether the bus arrival data should be shown initially
 */
class BusRouteViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val busArrivalGetter: BusArrivalGetter = BusApplication.instance.container.busArrivalGetter,
    private val locationRepository: LocationRepository = BusApplication.instance.container.locationRepository,
    private val serviceNo: String,
    private val direction: Int,
    private val stopSequence: Int,
    private val showMap: Boolean,
    private val showLiveBuses: Boolean
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusRouteViewModel::class.java)) {
            BusRouteViewModel(
                busRepository = busRepository,
                busArrivalGetter = busArrivalGetter,
                locationRepository = locationRepository,
                serviceNo = serviceNo,
                direction = direction,
                stopSequence = stopSequence,
                showMap = showMap,
                showLiveBuses = showLiveBuses
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

/**
 * This class is the View Model for the Bus Route Information page
 *
 * @param busRepository The repository of the bus data
 * @param busArrivalGetter The source of the bus arrival data
 * @param locationRepository The source of the user location
 * @param serviceNo The bus service number
 * @param direction The direction in which the bus travels (1 or 2), loop services only have 1 direction
 * @param stopSequence The bus stop sequence of the bus stop along the route
 * to be set as the 0th bus stop initially
 * @param showMap Whether the map should be shown initially
 * @param showLiveBuses Whether the bus arrival data should be shown initially
 */
class BusRouteViewModel(
    private val busRepository: BusRepository,
    private val busArrivalGetter: BusArrivalGetter,
    private val locationRepository: LocationRepository,
    serviceNo: String,
    direction: Int,
    stopSequence: Int,
    showMap: Boolean,
    showLiveBuses: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BusRouteUIState(
            showLiveBuses = showLiveBuses,
            showMap = showMap
        )
    )
    val uiState: StateFlow<BusRouteUIState> = _uiState.asStateFlow()

    private var lastTimeRefreshPressed: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    private val _cameraPositionState = MutableStateFlow(
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(LatLng(1.290270, 103.851959), 15f)
        )
    )
    val cameraPositionState = _cameraPositionState.asStateFlow()

    private val _markerState = MutableStateFlow(
        MarkerState(position = LatLng(1.290270, 103.851959))
    )

    val markerState = _markerState.asStateFlow()

    /**
     * Set the current bus service and route list with the specified initial bus service number and direction.
     * Also set the 0th bus stop to the bus stop with that stop sequence for the displayed route list.
     */
    init {
        updateBusService(serviceNo, direction, stopSequence)
        Log.d(
            "BusServiceViewModel",
            "BusServiceViewModel created with parameters: $serviceNo, $direction")
    }

    /**
     * Stop fetching of location when ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        locationRepository.stopFetchingLocation()
    }

    /**
     * Update the current bus service and route list with the Bus Service Information.
     *
     * @param busServiceInfo The bus service information
     */
    fun updateBusService(busServiceInfo: BusServiceInfo) {
        updateBusService(busServiceInfo.serviceNo, busServiceInfo.direction)
    }

    /**
     * Set the current bus service and route list with the specified bus service number and direction.
     * Also set the 0th bus stop to the bus stop with that stop sequence for the displayed route list.
     *
     * @param serviceNo The bus service number
     * @param direction The direction in which the bus travels (1 or 2), loop services only have 1 direction
     * @param stopSequence The bus stop sequence of the bus stop along the route to be set as the
     * 0th bus stop. If set to a negative value, the full bus route will be displayed.
     */
    private fun updateBusService(serviceNo: String, direction: Int, stopSequence: Int = -1) {
        viewModelScope.launch {
            val oldBusServiceInfo = uiState.value.busServiceInfo
            val busRoute: List<BusRouteInfoWithBusStopInfo> = busRepository
                .getBusServiceRoute(serviceNo = serviceNo, direction = direction)

            if (busRoute.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        busRoute = attachDistanceFromCurrentLocation(busRoute),
                        busStopSequenceOffset = 0,
                        originalBusRoute = busRoute,
                        busServiceInfo = busRepository
                            .getBusService(serviceNo = serviceNo, direction = direction),
                        truncated = false,
                        firstStopIsStartOfLoopingPoint = false,
                        liveBuses = listOf()
                    )
                }
                if (oldBusServiceInfo == null || oldBusServiceInfo.serviceNo != serviceNo) {
                    _uiState.update {
                        it.copy(
                            busServiceVariants = busRepository
                                .getBusService(serviceNo = serviceNo.filter { it.isDigit() }),
                        )
                    }
                }

                if (stopSequence >= 0) {
                    setFirstBusStop(stopSequence)
                }
                updateCameraPositionToFirstStop()
                if (uiState.value.showLiveBuses) {
                    refreshLiveBuses()
                }
            }
        }
    }

    /**
     * Set the current bus service and route list with the current bus service number but opposite direction.
     */
    fun toggleDirection() {
        val busServiceInfo = uiState.value.busServiceInfo
        if (!isLoop(uiState.value.originalBusRoute) && busServiceInfo != null) {
            updateBusService(
                serviceNo = busServiceInfo.serviceNo,
                direction = if (busServiceInfo.direction == 1) 2 else 1
            )
        } else {
            Toast.makeText(
                BusApplication.instance,
                "Other direction does not exist",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Set the 0th bus stop to the bus stop with that stop sequence in the displayed route list.
     *
     * @param stopSequence The bus stop sequence of the bus stop along the DISPLAYED route to be set as the
     * 0th bus stop. If set to a negative value, the full bus route will be displayed.
     */
    fun setFirstBusStop(stopSequence: Int) {
        viewModelScope.launch {
            val truncatedRoute = truncateTillBusStop(
                route = uiState.value.originalBusRoute,
                stopSequence = (stopSequence + uiState.value.busStopSequenceOffset)
                        % (uiState.value.originalBusRoute.size - 1)
            )
            _uiState.update {
                it.copy(
                    busRoute = attachDistanceFromCurrentLocation(truncatedRoute),
                    truncated = true,
                    firstStopIsStartOfLoopingPoint = false,
                    busStopSequenceOffset = (stopSequence + uiState.value.busStopSequenceOffset)
                            % (uiState.value.originalBusRoute.size - 1)
                )
            }
            updateCameraPositionToFirstStop()
            refreshLiveBuses()
        }
    }

    /**
     * Set the 0th bus stop to the first stop in the looping point. Only valid for
     * non pure clockwise/anticlockwise loop bus routes.
     */
    fun setLoopingPointAsFirstBusStop() {
        viewModelScope.launch {
            _uiState.update {
                val truncatedRoute = truncateLoopRoute(
                    route = uiState.value.originalBusRoute,
                    after = true
                )
                it.copy(
                    busRoute = attachDistanceFromCurrentLocation(truncatedRoute.second),
                    truncated = false,
                    firstStopIsStartOfLoopingPoint = true,
                    busStopSequenceOffset = truncatedRoute.first
                )
            }
            updateCameraPositionToFirstStop()
        }
    }

    /**
     * Set the 0th bus stop to the first stop of the full bus route list.
     *
     * @param truncateAfterLoopingPoint Whether to remove all bus stops in the route list
     * after the last stop in the looping point. If the bus route is not a loop, this has no effect
     */
    fun setOriginalFirstBusStop(truncateAfterLoopingPoint: Boolean = false) {
        val busRoute = uiState.value.originalBusRoute
        _uiState.update {
            it.copy(
                busRoute = attachDistanceFromCurrentLocation(busRoute),
                truncated = false,
                firstStopIsStartOfLoopingPoint = false,
                busStopSequenceOffset = 0
            )
        }
        if (truncateAfterLoopingPoint) {
            setFirstBusStop(0)
        }
        updateCameraPositionToFirstStop()
    }

    /**
     * Set whether the map is shown.
     *
     * @param showMap True if the map should be shown.
     */
    fun setShowMap(showMap: Boolean) {
        _uiState.update {
            it.copy(showMap = showMap)
        }
        if (!showMap) {
            setShowLiveBuses(false)
        }
    }

    /**
     * Set whether bus arrival data of the 0th stop should be shown.
     * Also refreshes the bus arrival data and shows the map.
     *
     * @param showLiveBuses True if the bus arrival data should be shown, false otherwise
     */
    fun setShowLiveBuses(showLiveBuses: Boolean) {
        _uiState.update {
            it.copy(showLiveBuses = showLiveBuses)
        }
        if (showLiveBuses) {
            setShowMap(true)
            refreshLiveBuses()
        }
    }

    /**
     * Set whether first/last bus timings for all stops in the route list should be shown.
     *
     * @param showFirstLastBus True if the timings should be shown, false otherwise
     */
    fun setShowFirstLastBusTimings(showFirstLastBus: Boolean) {
        _uiState.update {
            it.copy(showFirstLastBus = showFirstLastBus)
        }
    }

    /**
     * Set whether live user location and speed should be constantly fetched and shown on the map.
     *
     * @param isLiveLocation True if live user location should be turned on, false otherwise
     */
    fun setIsLiveLocation(isLiveLocation: Boolean) {

        if (!isLiveLocation) {
            locationRepository.stopFetchingLocation()
            _uiState.update { it.copy(isLiveLocation = false) }
        } else {
            locationRepository.startFetchingLocation(fastRefresh = true)
            _uiState.update { it.copy(isLiveLocation = true) }
        }

    }

    /**
     * Toggles whether bus service information should be shown.
     */
    fun toggleShowBusServiceInfo() {
        _uiState.update {
            it.copy(
                showBusServiceInfo = !uiState.value.showBusServiceInfo
            )
        }
    }

    /**
     * Listen for updates to user location. This method should only be called once.
     */
    fun updateLiveLocation() {
        viewModelScope.launch {
            if (uiState.value.isLiveLocation) {
                locationRepository.startFetchingLocation(fastRefresh = true)
            }
            locationRepository.currentLocation
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { location ->
                    if (uiState.value.isLiveLocation) {
                        updateLocation(
                            location = location
                        )
                    }
                }
        }
    }

    /**
     * Update the location of the user, the distances between the user location and
     * each bus stop in the bus stop list, and the location of the marker on the map.
     *
     * @param location The user location.
     */
    private fun updateLocation(location: Location) {
        _uiState.update {
            it.copy(
                busRoute = uiState.value.busRoute
                    .map { Pair(
                        first = it.second.busStopInfo
                            .distanceFromInMetres(LatLong(location.latitude, location.longitude)),
                        second = it.second
                    ) },
                currentSpeed = if (location.hasSpeed()) (location.speed * 3.6).toInt() else 0
            )
        }
        val target = LatLng(location.latitude, location.longitude)
        _markerState.update {
            MarkerState(target)
        }
    }

    /**
     * Update the camera of the map to the location of the 0th stop in the displayed route list.
     */
    private fun updateCameraPositionToFirstStop() {
        if (uiState.value.busRoute.isNotEmpty()) {
            val busStopInfo = uiState.value.busRoute.first().second.busStopInfo
            updateCameraPosition(LatLng(busStopInfo.latitude, busStopInfo.longitude))
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
                    if (zoomIn) 20f else it.position.zoom,
                    it.position.tilt,
                    it.position.bearing
                )
            )
        }
    }

    /**
     * Add the distance between the user location and each bus stop in the route list.
     *
     * @param route The route list
     * @return The route list including the distances
     */
    private fun attachDistanceFromCurrentLocation(route: List<BusRouteInfoWithBusStopInfo>)
    : List<Pair<Int, BusRouteInfoWithBusStopInfo>> {
        val location = locationRepository.currentLocation.value
        if (location != null) {
            return attachDistanceFromPoint(LatLong(location.latitude, location.longitude), route)
        } else {
            return route.map { Pair(0, it) }
        }
    }

    /**
     * Refresh the bus arrival data. If an exception occurs, show live buses is turned off.
     */
    fun refreshLiveBuses() {
        val busServiceInfo = uiState.value.busServiceInfo
        if (busServiceInfo != null && uiState.value.showLiveBuses) {
            val threshold = 5
            val currentTime = LocalDateTime.now()
            val difference = Duration.between(lastTimeRefreshPressed, currentTime).seconds
            if (difference > threshold) {
                viewModelScope.launch {
                    try {

                        val firstStop = uiState.value.busRoute.first().second.busStopInfo
                        val lastStop = uiState.value.originalBusRoute.last().busStopInfo

                        val liveBuses = busArrivalGetter
                            .getBusArrival(firstStop.busStopCode)
                            .getBusArrivalsOfASingleService(serviceNo = busServiceInfo.serviceNo)
                            .flatMap { listOf(it.nextBus, it.nextBus2, it.nextBus3) }
                            .filter {
                                it.destinationCode == lastStop.busStopCode
                            }
                            .map {
                                if (it.isLive()) {
                                    it
                                } else {
                                    val busOrigin = busRepository.getBusStop(it.originCode)

                                    if (busOrigin == null) {
                                        val busRouteOrigin = uiState.value.originalBusRoute.first().busStopInfo
                                        it.copy(
                                            latitude = busRouteOrigin.latitude,
                                            longitude = busRouteOrigin.longitude
                                        )
                                    } else {
                                        it.copy(
                                            latitude = busOrigin.latitude,
                                            longitude = busOrigin.longitude
                                        )
                                    }

                                }
                            }

                        _uiState.update {
                            it.copy(
                                liveBuses = liveBuses
                            )
                        }
                        lastTimeRefreshPressed = currentTime
                    } catch (e: IOException) {
                        _uiState.update {
                            it.copy(
                                liveBuses = listOf(),
                                showLiveBuses = false
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(
                    BusApplication.instance,
                    "Refresh live buses again in ${threshold - difference}s",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

}