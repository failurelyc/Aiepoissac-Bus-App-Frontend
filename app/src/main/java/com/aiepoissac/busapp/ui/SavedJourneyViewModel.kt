package com.aiepoissac.busapp.ui

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.userdata.BusJourneyInfo
import com.aiepoissac.busapp.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class SavedJourneyViewModelFactory(
    private val userDataRepository: UserDataRepository = BusApplication.instance.container.userDataRepository,
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val journeyID: String
): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SavedJourneyViewModel::class.java)) {
            SavedJourneyViewModel(
                userDataRepository = userDataRepository,
                busRepository = busRepository,
                journeyID = journeyID
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }

    }
}

class SavedJourneyViewModel (
    private val userDataRepository: UserDataRepository,
    private val busRepository: BusRepository,
    private val journeyID: String
): ViewModel() {

    private val _uiState = MutableStateFlow(SavedJourneyUIState())
    val uiState = _uiState.asStateFlow()

    private var lastTimeRefreshPressed: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    init {
        viewModelScope.launch {
            refreshList()
        }
    }

    private fun refreshList() {
        viewModelScope.launch {
            _uiState.update {
                SavedJourneyUIState(
                    busJourneys = userDataRepository.getBusJourneyList(journeyID)
                        .sortedBy { it.sequence }
                        .map { it.attachBusArrivalsAndBusRouteWithBusStopInfo(busRepository) },
                )
            }
        }
    }

    fun refreshBusArrivals() {
        val threshold = 20
        val currentTime = LocalDateTime.now()
        val difference = Duration.between(lastTimeRefreshPressed, currentTime).seconds
        if (difference > threshold) {
            lastTimeRefreshPressed = currentTime
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        busJourneys = uiState.value.busJourneys
                            .map { it.first.attachBusArrivalsAndBusRouteWithBusStopInfo(busRepository) }
                    )
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

    fun toggleAddDialog() {
        _uiState.update {
            it.copy(showAddDialog = !uiState.value.showAddDialog)
        }
    }

    fun addBusJourney() {
        val originStop = uiState.value.originStopInput
        val destinationStop = uiState.value.destinationStopInput

        if (originStop != null && destinationStop != null
            && originStop.busRouteInfo.serviceNo == destinationStop.busRouteInfo.serviceNo
            && originStop.busRouteInfo.direction == destinationStop.busRouteInfo.direction
            ) {

            viewModelScope.launch {
                userDataRepository.insertBusJourneyInfo(
                    BusJourneyInfo(
                        journeyID = journeyID,
                        serviceNo = originStop.busRouteInfo.serviceNo,
                        direction = originStop.busRouteInfo.direction,
                        originBusStopSequence = originStop.busRouteInfo.stopSequence,
                        destinationBusStopSequence = destinationStop.busRouteInfo.stopSequence,
                        sequence = uiState.value.busJourneys.size
                    )
                )
                refreshList()
            }

        }

    }

    fun deleteBusJourney(busJourney: BusJourneyInfo) {
        viewModelScope.launch {
            userDataRepository.deleteBusJourneyInfo(busJourney)
            uiState.value.busJourneys
                .forEach {
                    if (it.first.sequence > busJourney.sequence) {
                        userDataRepository.deleteBusJourneyInfo(it.first)
                        userDataRepository.insertBusJourneyInfo(
                            it.first.copy(sequence = it.first.sequence - 1)
                        )
                    }
                }
            refreshList()
        }
    }

    fun updateServiceNoInput(serviceNo: String) {
        _uiState.update {
            it.copy(
                serviceNoInput = serviceNo
            )
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    originStopInput = null,
                    destinationStopInput = null,
                    originStopSearchResults = busRepository
                        .getBusServiceRoute(
                            serviceNo = serviceNo,
                            direction = if (uiState.value.directionOne) 1 else 2
                        ),
                    destinationStopSearchResults = listOf()
                )
            }
        }
    }

    fun toggleDirection() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    directionOne = !uiState.value.directionOne,
                    originStopInput = null,
                    destinationStopInput = null,
                    originStopSearchResults = busRepository
                        .getBusServiceRoute(
                            serviceNo = uiState.value.serviceNoInput,
                            direction = if (!uiState.value.directionOne) 1 else 2
                        ),
                    destinationStopSearchResults = listOf()
                )
            }
        }
    }

    fun updateOriginStopInput(originStop: BusRouteInfoWithBusStopInfo) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    originStopInput = originStop,
                    destinationStopInput = null,
                    destinationStopSearchResults = busRepository.getBusServiceRouteAfterSpecifiedStop(
                        serviceNo = uiState.value.serviceNoInput,
                        direction = if (uiState.value.directionOne) 1 else 2,
                        stopSequence = originStop.busRouteInfo.stopSequence)
                )
            }
        }
    }

    fun updateDestinationStopInput(destinationStop: BusRouteInfoWithBusStopInfo) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    destinationStopInput = destinationStop
                )
            }
        }
    }

    fun setOriginStopSearchExpanded(expanded: Boolean) {
        _uiState.update {
            it.copy(originStopSearchExpanded = expanded)
        }
    }

    fun setDestinationStopSearchExpanded(expanded: Boolean) {
        _uiState.update {
            it.copy(destinationStopSearchExpanded = expanded)
        }
    }

}