package com.aiepoissac.busapp.ui


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.R
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.getBusArrival
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


class BusArrivalViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(BusArrivalUIState())
    val uiState: StateFlow<BusArrivalUIState> = _uiState.asStateFlow()

    var busStopCodeInput by mutableStateOf("")
        private set


    fun updateBusStopCodeInput(busStopCodeInput: String) {
        if (busStopCodeInput.length <= 5) {
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

    fun getBusArrivalColor(bus: Bus): Color {
        return if(bus.isValid()) {
            if (bus.monitored == 0) {
                Color.White
            } else if (bus.load == "SEA") {
                Color.Green
            } else if (bus.load == "SDA") {
                Color.Yellow
            } else if (bus.load == "LSD") {
                Color.Red
            } else {
                Color.Black //Invalid data
            }
        } else {
            Color.LightGray
        }

    }

    init {
        viewModelScope.launch {
            while (true) {
                refreshBusArrival()
                delay(60000)
            }
        }
    }

}



