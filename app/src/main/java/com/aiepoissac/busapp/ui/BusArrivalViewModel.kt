package com.aiepoissac.busapp.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.GoogleMapsURLGenerator
import com.aiepoissac.busapp.LocationManager
import com.aiepoissac.busapp.R
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.getBusArrival
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.ui.theme.GreenDark
import com.aiepoissac.busapp.ui.theme.GreenLight
import com.aiepoissac.busapp.ui.theme.RedDark
import com.aiepoissac.busapp.ui.theme.RedLight
import com.aiepoissac.busapp.ui.theme.YellowDark
import com.aiepoissac.busapp.ui.theme.YellowLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime

class BusArrivalViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val busStopCodeInput: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusArrivalViewModel::class.java)) {
            BusArrivalViewModel(
                busRepository = busRepository,
                initialBusStopCodeInput = busStopCodeInput) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class BusArrivalViewModel(
    private val busRepository: BusRepository,
    initialBusStopCodeInput: String) : ViewModel() {

    private val _uiState = MutableStateFlow(BusArrivalUIState())
    val uiState: StateFlow<BusArrivalUIState> = _uiState.asStateFlow()

    private var lastTimeRefreshPressed: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    fun updateBusStopCodeInput(busStopCodeInput: String) {
        _uiState.update {
            it.copy(
                busStopCodeInput = busStopCodeInput
            )
        }
        viewModelScope.launch {
            if (busStopCodeInput.isNotEmpty()) {
                if (busStopCodeInput.first() < '0' || busStopCodeInput.first() > '9') {
                    _uiState.update {
                        it.copy(
                            searchResult = busRepository.getBusStopsContaining(busStopCodeInput),
                            expanded = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            searchResult = busRepository.getBusStopsWithPrefixCode(busStopCodeInput),
                            expanded = true
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        expanded = false
                    )
                }
            }
        }
    }

    fun setExpanded(expanded: Boolean) {
        _uiState.update { it.copy(expanded = expanded) }
    }

    fun updateBusStop() {
        viewModelScope.launch {
            updateBusStop(uiState.value.busStopCodeInput)
        }
    }

    fun switchToOppositeBusStop() {
        viewModelScope.launch {
            val busStopInfo = uiState.value.busStopInfo

                val thisBusStopCode = busStopInfo?.busStopCode ?: uiState.value.busStopCodeInput
                var oppositeBusStopCode = ""
                if (thisBusStopCode.last() == '9') {
                    oppositeBusStopCode = thisBusStopCode.substring(
                        startIndex = 0,
                        endIndex = thisBusStopCode.length - 1
                    ) + "1"
                } else if (thisBusStopCode.last() == '1') {
                    oppositeBusStopCode = thisBusStopCode.substring(
                        startIndex = 0,
                        endIndex = thisBusStopCode.length - 1
                    ) + "9"
                }
                if (oppositeBusStopCode.isNotEmpty()) {
                    updateBusStop(oppositeBusStopCode)
                } else {
                    Toast.makeText(BusApplication.instance, "No opposite bus stop", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private suspend fun updateBusStop(busStopCode: String) {

        lastTimeRefreshPressed = LocalDateTime.MIN
        val busStopInfo = busRepository.getBusStop(busStopCode)
        val busRoutes = busRepository.getBusRoutesAtBusStop(busStopCode)

        if (busStopInfo != null) {
            _uiState.update {
                it.copy(
                    busStopInfo = busStopInfo,
                    busRoutes = busRoutes,
                    busStopCodeInput = busStopCode,
                    busArrivalData = null,
                    expanded = false
                )
            }
            refreshBusArrival()
        } else {
            _uiState.update {
                it.copy(
                    busStopInfo = null,
                    busArrivalData = null,
                    busRoutes = busRoutes,
                    busStopCodeInput = busStopCode,
                    showBusArrival = false,
                    expanded = false
                )
            }
        }
    }

    fun refreshBusArrival() {
        val busStopInfo = uiState.value.busStopInfo
        if (busStopInfo != null) {
            val threshold = 20
            val currentTime = LocalDateTime.now()
            val difference = Duration.between(lastTimeRefreshPressed, currentTime).seconds
            if (difference > threshold) {
                lastTimeRefreshPressed = currentTime
                _uiState.update {
                    it.copy(
                        isRefreshing = true
                    )
                }
                viewModelScope.launch {
                    delay(16) //to allow time for the loading animation to start
                    try {
                        val busArrivalData = getBusArrival(busStopInfo.busStopCode)
                            .services
                            .map {
                                it.attachOriginDestinationBusStopInfo(busRepository)
                            }
                        _uiState.update {
                            it.copy(
                                busArrivalData = busArrivalData,
                                connectionIssue = false,
                                isRefreshing = false
                            )
                        }
                    } catch (e: IOException) {
                        lastTimeRefreshPressed = LocalDateTime.MIN
                        _uiState.update {
                            it.copy(
                                connectionIssue = true,
                                isRefreshing = false
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(
                    BusApplication.instance,
                    "Try again in ${threshold - difference}s",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getDistance(bus: Bus): String {
        return if (bus.isLive()) {
            "${bus.getDistanceFrom(uiState.value.busStopInfo)}m"
        } else {
            "-"
        }
    }

    fun toggleShowBusArrival() {
        _uiState.update {
            it.copy(showBusArrival = !uiState.value.showBusArrival)
        }
    }

    fun toggleHideBusType() {
        _uiState.update {
            it.copy(hideBusType = !uiState.value.hideBusType)
        }
    }

    fun getBusRoute(serviceNo: String): BusRouteInfo? {
        val busRoutes = uiState.value.busRoutes
            .filter { it.serviceNo == serviceNo }
        return if (busRoutes.size == 1) { //bus service stops here only once, no ambiguity
            busRoutes[0]
        } else if (busRoutes.map {it.direction} .distinct().size == 1 ) {
            //bus stops here more than once in the same direction
            busRoutes.minBy { it.stopSequence } //returns the first stop along the route
        } else { //bus stops here more than once in different directions
            null //cannot resolve the ambiguity
        }
    }

    fun openDirections() {
        val busStopInfo = uiState.value.busStopInfo
        if (busStopInfo != null) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                GoogleMapsURLGenerator.directions(
                    destination = busStopInfo,
                    travelMode = GoogleMapsURLGenerator.TravelMode.Transit
                ).toUri()
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(BusApplication.instance, intent, null)
        }
    }

    init {
        LocationManager.stopFetchingLocation()
        this.updateBusStopCodeInput(initialBusStopCodeInput)
        this.updateBusStop()
        Log.d(
            "BusArrivalViewModel",
            "BusArrivalViewModel created with parameter: $initialBusStopCodeInput")
        viewModelScope.launch {
            while (true) {
                delay(60000)
                refreshBusArrival()
            }
        }
    }

}

fun busTypeToPicture(bus: Bus): Int {
    return if (bus.type == "SD") {
        R.drawable.sd
    } else if (bus.type == "DD") {
        R.drawable.dd
    } else if (bus.type == "BD") {
        R.drawable.bd
    } else {
        R.drawable.na
    }
}

fun getBusArrivalColor(bus: Bus, darkMode: Boolean = false): Color {
    return if(bus.isValid()) {
        if (!bus.isLive()) {
            if (!darkMode) Color.White else Color.LightGray
        } else {
            return getBusArrivalColor(bus.load, darkMode)
        }
    } else {
        if (!darkMode) Color.LightGray else Color.DarkGray
    }

}

fun getBusArrivalColor(s: String = "", darkMode: Boolean = false) : Color {

    return if (s == "SEA") {
        if (!darkMode) GreenLight else GreenDark
    } else if (s == "SDA") {
        if (!darkMode) YellowLight else YellowDark
    } else if (s == "LSD") {
        if (!darkMode) RedLight else RedDark
    } else {
        if (!darkMode) Color.White else Color.LightGray //Invalid data
    }
}

