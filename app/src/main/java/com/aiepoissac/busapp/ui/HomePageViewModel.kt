package com.aiepoissac.busapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomePageViewModel : ViewModel() {

    var busArrivalBusStopCodeInput by mutableStateOf("")
        private set

    var busServiceInput by mutableStateOf("")
        private set

    fun updateBusArrivalBusStopCodeInput(input: String) {
        if (input.length <= 5) {
            this.busArrivalBusStopCodeInput = input
        }
    }

    fun updateBusServiceInput(input: String) {
        if (input.length <= 4) {
            this.busServiceInput = input
        }
    }
}