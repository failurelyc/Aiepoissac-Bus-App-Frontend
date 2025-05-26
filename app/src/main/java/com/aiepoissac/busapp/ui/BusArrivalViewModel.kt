package com.aiepoissac.busapp.ui


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.R
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.getBusArrival
import com.aiepoissac.busapp.data.businfo.BusRepository
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

    var busStopCodeInput by mutableStateOf(initialBusStopCodeInput)
        private set

    fun updateBusStopCodeInput(busStopCodeInput: String) {
        if (busStopCodeInput.length <= 5 || this.busStopCodeInput.length > 5) {
            this.busStopCodeInput = busStopCodeInput
        }
    }

    fun updateBusStop() {
        viewModelScope.launch {
            updateBusStop(busStopCodeInput)
        }
    }

    fun switchToOppositeBusStop() {
        viewModelScope.launch {
            val busStopInfo = uiState.value.busStopInfo
            if (busStopInfo != null) {
                val thisBusStopCode = busStopInfo.busStopCode
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
                if (oppositeBusStopCode.isNotEmpty() &&
                    busRepository.getBusStop(oppositeBusStopCode) != null) {
                    updateBusStop(oppositeBusStopCode)
                }
            }
        }
    }

    private suspend fun updateBusStop(busStopCode: String) {
        val busStopInfo = busRepository.getBusStop(busStopCode)
        val busRoutes = busRepository.getBusRoutesAtBusStop(busStopCode)

        try {
            val busArrivalData = getBusArrival(busStopCode)
            _uiState.update {
                it.copy(
                    busStopInfo = busStopInfo,
                    busArrivalData = busArrivalData,
                    busRoutes = busRoutes
                )
            }
        } catch (e: IOException) {
            _uiState.update {
                it.copy(
                    busStopInfo = busStopInfo,
                    busArrivalData = null,
                    busRoutes = busRoutes) }
        }
    }



    fun refreshBusArrival() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            delay(16) //to allow time for the loading animation to start
            try {
                val busArrivalData = getBusArrival(uiState.value.busStopInfo?.busStopCode ?: "")
                _uiState.update {
                    it.copy(
                        busArrivalData = busArrivalData,
                        isRefreshing = false
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        busArrivalData = null,
                        isRefreshing = false) }
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

    init {
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



