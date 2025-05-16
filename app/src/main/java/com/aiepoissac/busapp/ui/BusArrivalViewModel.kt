package com.aiepoissac.busapp.ui


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        this.busStopCodeInput = busStopCodeInput
    }

    fun updateBusStop() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    BusArrivalUIState(busStopCodeInput = busStopCodeInput,
                        busArrivalData = getBusArrival(busStopCodeInput.toInt())
                    ) }
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

            delay(16)
            busStopCodeInput = uiState.value.busStopCodeInput
            updateBusStop()

        }
    }



}



