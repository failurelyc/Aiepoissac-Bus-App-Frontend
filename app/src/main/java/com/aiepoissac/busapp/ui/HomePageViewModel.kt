package com.aiepoissac.busapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomePageViewModel : ViewModel() {

    var busArrivalBusStopCodeInput by mutableStateOf("")
        private set

    var busServiceNoInput by mutableStateOf("")
        private set

    fun updateBusArrivalBusStopCodeInput(input: String) {
        if (input.length <= 5) {
            this.busArrivalBusStopCodeInput = input
        }
    }

    fun updateBusServiceNoInput(input: String) {
        if (input.length <= 4) {
            this.busServiceNoInput = input
        }
    }
}