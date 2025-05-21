package com.aiepoissac.busapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.data.businfo.BusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
                BusRouteUIState(
                    busRoute = busRepository
                        .getBusServiceRoute(serviceNo = serviceNo, direction = direction)
                        .first()
                )
            }
        }
        Log.d(
            "BusServiceViewModel",
            "BusServiceViewModel created with parameters: $serviceNo, $direction")
    }

}