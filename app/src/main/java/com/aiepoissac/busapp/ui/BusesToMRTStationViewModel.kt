package com.aiepoissac.busapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
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
                start = Pair(latitude, longitude),
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
    start: Pair<Double, Double>,
    stationCode: String,
    distanceThreshold: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusesToMRTStationUIState(
        distanceThreshold = distanceThreshold))
    val uiState: StateFlow<BusesToMRTStationUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val mrtStation = busRepository.getMRTStation(stationCode)
            val distanceToMRTStation = mrtStation.distanceFromInMetres(start)
            val busStops = busRepository.getAllBusStops()
            val busRoutesNearby: List<Pair<Int, BusRouteInfoWithBusStopInfo>> = busStops
                .map { Pair(it.distanceFromInMetres(start), it) }
                .filter { it.first < uiState.value.distanceThreshold }
                .flatMap { distanceAndBusStop ->
                    busRepository
                        .getBusRoutesAtBusStop(distanceAndBusStop.second.busStopCode)
                        .map { busRoute ->
                            Pair(
                                first = distanceAndBusStop.first,
                                second = BusRouteInfoWithBusStopInfo(
                                    busRouteInfo = busRoute,
                                    busStopInfo = distanceAndBusStop.second
                                )
                            ) }
                }
                .sortedBy { it.first }
            val seenBusRoutes: HashSet<BusRouteInfoWithBusStopInfo> = HashSet()
            val routes: MutableList<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> = mutableListOf()

            for (busRouteNearby in busRoutesNearby) {
                if (!seenBusRoutes.contains(busRouteNearby.second)) {
                    val remainingDistanceThreshold = distanceThreshold - busRouteNearby.first
                    val busRouteInfo = busRouteNearby.second.busRouteInfo
                    val fullBusRoute = busRepository.getBusServiceRoute(
                        serviceNo = busRouteInfo.serviceNo,
                        direction = busRouteInfo.direction
                    )
                    val routeFromThisStop = truncateTillBusStop(
                        route = fullBusRoute,
                        stopSequence = busRouteInfo.stopSequence,
                        adjustStopSequence = false
                    )
                    var destination: Pair<Int, BusRouteInfoWithBusStopInfo>? = null
                    for (stop in routeFromThisStop) {
                        if (!seenBusRoutes.contains(stop)) {
                            seenBusRoutes.add(stop)
                            val distance = stop.busStopInfo.distanceFromInMetres(mrtStation)
                            if (distance <= remainingDistanceThreshold) {
                                if (destination == null || destination.first > distance) {
                                    destination = Pair(distance, stop)
                                } else {
                                    break
                                }
                            }
                        } else {
                            break
                        }
                    }
                    if (destination != null && destination != busRouteNearby
                        && destination.first + busRouteNearby.first < distanceToMRTStation) {
                        routes.add(Pair(busRouteNearby, destination))
                    }
                }
            }
            _uiState.update {
                it.copy(routes = routes, mrtStation = mrtStation)
            }
        }
    }
}