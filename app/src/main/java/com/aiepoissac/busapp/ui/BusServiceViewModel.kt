package com.aiepoissac.busapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusServiceViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val busServiceInput: String
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusServiceViewModel::class.java)) {
            BusServiceViewModel(
                busRepository = busRepository,
                initialBusServiceInput = busServiceInput
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

class BusServiceViewModel(
    private val busRepository: BusRepository,
    initialBusServiceInput: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusServiceUIState())
    val uiState: StateFlow<BusServiceUIState> = _uiState.asStateFlow()

    var busServiceNoInput by mutableStateOf(initialBusServiceInput)
        private set

    fun updateBusServiceNoInput(busServiceNoInput: String) {
        if (busServiceNoInput.length <= 4 || this.busServiceNoInput.length > 4) {
            this.busServiceNoInput = busServiceNoInput
        }
    }

    fun updateBusService() {
        viewModelScope.launch {
            _uiState.update {
                BusServiceUIState(
                    busServiceNoInput = busServiceNoInput,
                    busServiceList = busRepository.getBusService(busServiceNoInput).first()
                )
            }
            println("updated")
        }
    }

    init {
        this.updateBusServiceNoInput(initialBusServiceInput)
        this.updateBusService()
        Log.d(
            "BusServiceViewModel",
            "BusServiceViewModel created with parameter: $initialBusServiceInput")
    }
}

