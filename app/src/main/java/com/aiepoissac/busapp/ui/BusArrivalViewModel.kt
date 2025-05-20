package com.aiepoissac.busapp.ui


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.R
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.getBusArrival
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


class BusArrivalViewModelFactory(private val busStopCodeInput: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusArrivalViewModel::class.java)) {
            BusArrivalViewModel(busStopCodeInput) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}

class BusArrivalViewModel(initialBusStopCodeInput: String) : ViewModel() {

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
            try {
                _uiState.update {
                    BusArrivalUIState(busStopCodeInput = busStopCodeInput,
                        busArrivalData = getBusArrival(busStopCodeInput.toInt())
                    )
                }
            } catch (e: NumberFormatException) {
                _uiState.update {
                    BusArrivalUIState(busStopCodeInput = busStopCodeInput) }
            } catch (e: IOException) {
                _uiState.update {
                    BusArrivalUIState(networkIssue = true,
                        busStopCodeInput = busStopCodeInput) }
            }
        }
    }
    fun refreshBusArrival() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        viewModelScope.launch {
            delay(16) //to allow time for the loading animation to start
            busStopCodeInput = uiState.value.busStopCodeInput
            updateBusStop()
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
            if (bus.monitored == 0) {
                if (!darkMode) Color.White else Color.LightGray
            } else {
                return getBusArrivalColor(bus.load, darkMode)
            }
        } else {
            if (!darkMode) Color.LightGray else Color.DarkGray
        }

    }

    fun getBusArrivalColor(s: String, darkMode: Boolean = false) : Color {

        return if (s == "SEA") {
            if (!darkMode) GreenLight else GreenDark
        } else if (s == "SDA") {
            if (!darkMode) YellowLight else YellowDark
        } else if (s == "LSD") {
            if (!darkMode) RedLight else RedDark
        } else {
            Color.Black //Invalid data
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(
            "BusArrivalViewModel",
            "BusArrivalViewModel is being destroyed.")
    }

    init {
        this.updateBusStopCodeInput(initialBusStopCodeInput)
        this.updateBusStop()
        Log.d(
            "BusArrivalViewModel",
            "BusArrivalViewModel created with parameter: $initialBusStopCodeInput")
        viewModelScope.launch {
            while (true) {
                refreshBusArrival()
                delay(60000)
            }
        }
    }

}



