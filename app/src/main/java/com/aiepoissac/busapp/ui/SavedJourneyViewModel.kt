package com.aiepoissac.busapp.ui

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.busarrival.BusArrivalGetter
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.userdata.JourneySegmentInfo
import com.aiepoissac.busapp.userdata.UserDataRepository
import com.aiepoissac.busapp.userdata.attachBusArrivalsToBusJourneyWithBusRouteInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class SavedJourneyViewModelFactory(
    private val userDataRepository: UserDataRepository = BusApplication.instance.container.userDataRepository,
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository,
    private val busArrivalGetter: BusArrivalGetter = BusApplication.instance.container.busArrivalGetter,
    private val journeyID: String
): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SavedJourneyViewModel::class.java)) {
            SavedJourneyViewModel(
                userDataRepository = userDataRepository,
                busRepository = busRepository,
                busArrivalGetter = busArrivalGetter,
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
    private val busArrivalGetter: BusArrivalGetter,
    private val journeyID: String
): ViewModel() {

    private val _uiState = MutableStateFlow(SavedJourneyUIState())
    val uiState = _uiState.asStateFlow()

    private var lastTimeRefreshPressed: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    init {
        viewModelScope.launch {
            refreshList(afterNewAddition = true)
        }
    }

    private fun refreshList(afterNewAddition: Boolean) {
        viewModelScope.launch {
            val busJourneys = userDataRepository.getAllJourneySegments(journeyID)
                .sortedBy { it.sequence }

            if (afterNewAddition) {
                _uiState.update { savedJourneyUIState ->
                    savedJourneyUIState.copy(
                        busJourneys = busJourneys
                            .map {
                                it.attachBusArrivalsAndBusRouteWithBusStopInfo(
                                    busRepository = busRepository,
                                    busArrivalGetter = busArrivalGetter
                                )
                            }
                    )
                }
            } else {
                val oldBusJourneys = uiState.value.busJourneys
                _uiState.update { savedJourneyUIState ->
                    savedJourneyUIState.copy(
                        busJourneys = busJourneys.map { busJourneyInfo ->
                            Pair(
                                first = busJourneyInfo,
                                second =
                                    oldBusJourneys.first {
                                        it.first.isSameJourneyAs(busJourneyInfo)
                                    }.second

                            )
                        }
                    )
                }
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
                _uiState.update { savedJourneyUIState ->
                    savedJourneyUIState.copy(
                        busJourneys = uiState.value.busJourneys
                            .map {
                                attachBusArrivalsToBusJourneyWithBusRouteInfo(
                                    Pair(
                                        first = it.first,
                                        second = Pair(
                                            first = it.second.first.first,
                                            second = it.second.second.first
                                        )
                                    ),
                                    busRepository = busRepository,
                                    busArrivalGetter = busArrivalGetter
                                )
                            },
                        showAddDialog = false
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

    fun setShowAddDialog(showAddDialog: Boolean) {
        _uiState.update {
            it.copy(showAddDialog = showAddDialog)
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
                userDataRepository.insertJourneySegment(
                    JourneySegmentInfo(
                        journeyID = journeyID,
                        serviceNo = originStop.busRouteInfo.serviceNo,
                        direction = originStop.busRouteInfo.direction,
                        originBusStopSequence = originStop.busRouteInfo.stopSequence,
                        destinationBusStopSequence = destinationStop.busRouteInfo.stopSequence,
                        sequence = uiState.value.busJourneys.size
                    )
                )
                clearInput()
                setShowAddDialog(false)
                refreshList(afterNewAddition = true)
            }

        }

    }

    fun deleteBusJourney(busJourney: JourneySegmentInfo) {
        viewModelScope.launch {
            userDataRepository.deleteJourneySegment(busJourney)
            uiState.value.busJourneys
                .forEach {
                    if (it.first.sequence > busJourney.sequence) {
                        userDataRepository.deleteJourneySegment(it.first)
                        userDataRepository.insertJourneySegment(
                            it.first.copy(sequence = it.first.sequence - 1)
                        )
                    }
                }
            refreshList(afterNewAddition = false)
        }
    }

    fun hideBusJourney(busJourney: JourneySegmentInfo) {

        viewModelScope.launch {

        }

    }

    fun moveBusJourneyUp(busJourney: JourneySegmentInfo) {
        if (busJourney.sequence > 0) {
            viewModelScope.launch {
                swapBusJourneySequence(busJourney, uiState.value.busJourneys[busJourney.sequence - 1].first)
            }
        }
    }

    fun moveBusJourneyDown(busJourney: JourneySegmentInfo) {
        if (busJourney.sequence < uiState.value.busJourneys.size - 1) {
            viewModelScope.launch {
                swapBusJourneySequence(busJourney, uiState.value.busJourneys[busJourney.sequence + 1].first)
            }
        }
    }

    private suspend fun swapBusJourneySequence(busJourney1: JourneySegmentInfo, busJourney2: JourneySegmentInfo) {
        userDataRepository.deleteJourneySegment(busJourney1)
        userDataRepository.deleteJourneySegment(busJourney2)
        val newBusJourney1 = busJourney1.copy(sequence = busJourney2.sequence)
        val newBusJourney2 = busJourney2.copy(sequence = busJourney1.sequence)
        userDataRepository.insertJourneySegment(newBusJourney1)
        userDataRepository.insertJourneySegment(newBusJourney2)
        refreshList(afterNewAddition = false)
    }

    fun setShowBusType(showBusType: Boolean) {
        _uiState.update {
            it.copy(showBusType = showBusType)
        }
    }

    fun setShowFirstLastBus(showFirstLastBus: Boolean) {
        _uiState.update {
            it.copy(showFirstLastBus = showFirstLastBus)
        }
    }

    fun setShowDestinationBusArrivals(showDestinationBusArrivals: Boolean) {
        _uiState.update {
            it.copy(showDestinationBusArrivals = showDestinationBusArrivals)
        }
    }

    fun clearInput() {
        _uiState.update {
            it.copy(
                serviceNoInput = "",
                isDirectionTwo = false,
                originStopInput = null,
                destinationStopInput = null,
                originStopSearchResults = listOf(),
                destinationStopSearchResults = listOf(),
                originStopSearchExpanded = false,
                destinationStopSearchExpanded = false
            )
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
                            direction = if (uiState.value.isDirectionTwo) 2 else 1
                        ),
                    destinationStopSearchResults = listOf()
                )
            }
        }
    }

    fun setIsDirectionTwo(isDirectionTwo: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDirectionTwo = isDirectionTwo,
                    originStopInput = null,
                    destinationStopInput = null,
                    originStopSearchResults = busRepository
                        .getBusServiceRoute(
                            serviceNo = uiState.value.serviceNoInput,
                            direction = if (isDirectionTwo) 2 else 1
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
                        direction = if (uiState.value.isDirectionTwo) 2 else 1,
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