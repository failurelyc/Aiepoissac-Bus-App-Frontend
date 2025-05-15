package com.aiepoissac.busapp.ui


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class BusArrivalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BusArrivalUIState())
    val uiState: StateFlow<BusArrivalUIState> = _uiState.asStateFlow()



}