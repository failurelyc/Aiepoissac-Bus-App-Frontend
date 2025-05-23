package com.aiepoissac.busapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusRouteViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val serviceNo: String,
    private val direction: Int
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusRouteViewModel::class.java)) {
            BusRouteViewModel(
                busRepository = busRepository,
                serviceNo = serviceNo,
                direction = direction
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class BusRouteViewModel(
    private val busRepository: BusRepository,
    serviceNo: String,
    direction: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRouteUIState())
    val uiState: StateFlow<BusRouteUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                val busRoute: List<BusRouteInfoWithBusStopInfo> = busRepository
                    .getBusServiceRoute(serviceNo = serviceNo, direction = direction)
                BusRouteUIState(
                    busRoute = busRoute,
                    originalBusRoute = busRoute,
                    busServiceInfo = busRepository
                        .getBusService(serviceNo = serviceNo, direction = direction)
                )
            }
        }
        Log.d(
            "BusServiceViewModel",
            "BusServiceViewModel created with parameters: $serviceNo, $direction")
    }

    fun setFirstBusStop(stopSequence: Int) {

        viewModelScope.launch {

            val truncatedRoute = uiState.value.busRoute
                .dropWhile { it.busRouteInfo.stopSequence < stopSequence }
            val distanceOffset = truncatedRoute.first().busRouteInfo.distance
            val truncatedRouteWithAdjustedInfo = truncatedRoute
                .map { stop ->
                    val adjustedSequence = stop.busRouteInfo.stopSequence - stopSequence
                    val adjustedDistance = stop.busRouteInfo.distance - distanceOffset
                    stop.copy(busRouteInfo = stop.busRouteInfo.copy(
                        stopSequence = adjustedSequence,
                        distance = adjustedDistance)
                    )
            }
            if (!isLoop()) {
                _uiState.update {
                    it.copy(
                        busRoute = truncatedRouteWithAdjustedInfo,
                        truncated = true)
                }
            } else {
                val seenBusStopCodes: HashSet<String> = HashSet()
                var seen = false
                val truncatedLoopedRouteWithAdjustedInfo = truncatedRouteWithAdjustedInfo
                    .takeWhile {
                        val thisStop = it.busStopInfo.busStopCode
                        lateinit var oppositeStop: String
                        if (thisStop.last() == '9') {
                            oppositeStop = thisStop.substring(0, thisStop.length - 1) + "1"
                        } else if (thisStop.last() == '1') {
                            oppositeStop = thisStop.substring(0, thisStop.length - 1) + "9"
                        } else {
                            return@takeWhile true
                        }
                        if (seenBusStopCodes.contains(oppositeStop)) {
                            if (seen) {
                                return@takeWhile false
                            } else {
                                seen = true
                            }
                        } else {
                            seen = false

                        }
                        seenBusStopCodes.add(thisStop)
                        return@takeWhile true
                    }
                    .dropLast(1)
                _uiState.update {
                    it.copy(
                        busRoute = truncatedLoopedRouteWithAdjustedInfo,
                        truncated = true)
                }
            }
        }
    }

    fun setOriginalFirstBusStop() {
        val busRoute = uiState.value.originalBusRoute
        _uiState.update {
            it.copy(
                busRoute = busRoute,
                truncated = false
            )
        }
    }

    fun toggleShowFirstLastBusToTrue() {
        val showFirstLastBus = uiState.value.showFirstLastBus
        _uiState.update {
            it.copy(showFirstLastBus = !showFirstLastBus)
        }
    }

    fun getShowFirstLastBus(): Boolean {
        return uiState.value.showFirstLastBus
    }

    fun isTruncatedRoute(): Boolean {
        return uiState.value.truncated
    }

    fun isLoop(): Boolean {
        return uiState.value.busServiceInfo?.isLoop() ?: false
    }

}