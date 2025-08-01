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
import com.aiepoissac.busapp.data.businfo.truncateTillBusStop
import com.aiepoissac.busapp.userdata.JourneySegmentInfo
import com.aiepoissac.busapp.userdata.UserDataRepository
import com.aiepoissac.busapp.userdata.attachBusArrivalsToBusJourneyWithBusRouteInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

/**
 * This class is the View Model factory for the View Model for the Saved Journey page
 *
 * @param userDataRepository The repository of the user data
 * @param busRepository The repository of the bus data
 * @param busArrivalGetter The source of the bus arrival data
 * @param journeyID The ID of this Saved Journey
 */
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

/**
 * This class is the View Model for the Saved Journey page
 *
 * @param userDataRepository The repository of the user data
 * @param busRepository The repository of the bus data
 * @param busArrivalGetter The source of the bus arrival data
 * @param journeyID The ID of this Saved Journey
 */
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

    /**
     * Refresh the list of segments displayed.
     *
     * @param afterNewAddition Whether this method was called after a new segment was added.
     * If true, the full list of segments, including bus route information and bus arrival
     * data, is recomputed.
     */
    private fun refreshList(afterNewAddition: Boolean) {
        viewModelScope.launch {
            val busJourneys = userDataRepository.getAllJourneySegments(journeyID)
                .sortedBy { it.sequence }

            if (afterNewAddition) {
                _uiState.update { savedJourneyUIState ->
                    savedJourneyUIState.copy(
                        segments = busJourneys
                            .map {
                                it.attachBusArrivalsAndBusRouteWithBusStopInfo(
                                    busRepository = busRepository,
                                    busArrivalGetter = busArrivalGetter
                                )
                            }
                    )
                }
            } else {
                val oldBusJourneys = uiState.value.segments
                _uiState.update { savedJourneyUIState ->
                    savedJourneyUIState.copy(
                        segments = busJourneys.map { busJourneyInfo ->
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

    /**
     * Refresh the bus arrival data.
     */
    fun refreshBusArrivals() {
        val threshold = 20
        val currentTime = LocalDateTime.now()
        val difference = Duration.between(lastTimeRefreshPressed, currentTime).seconds
        if (difference > threshold) {
            lastTimeRefreshPressed = currentTime
            viewModelScope.launch {
                _uiState.update { savedJourneyUIState ->
                    savedJourneyUIState.copy(
                        segments = uiState.value.segments
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

    /**
     * Sets whether Add Segment dialog should be shown.
     *
     * @param showAddDialog True if Add Segment dialog should be shown
     */
    fun setShowAddDialog(showAddDialog: Boolean) {
        _uiState.update {
            it.copy(showAddDialog = showAddDialog)
        }
    }

    /**
     * Add a segment to the Journey and the database using all the input values.
     * Also updates the displayed segment list and clears all input values.
     */
    fun addBusJourney() {
        val originStop = uiState.value.originStopInput?.busRouteInfo
        val destinationStop = uiState.value.destinationStopInput?.busRouteInfo

        if (originStop != null && destinationStop != null
            && originStop.serviceNo == destinationStop.serviceNo
            && originStop.direction == destinationStop.direction
            ) {

            viewModelScope.launch {

                val routeLength = busRepository.getBusServiceRouteLength(originStop.serviceNo, originStop.direction)

                userDataRepository.insertJourneySegment(
                    JourneySegmentInfo(
                        journeyID = journeyID,
                        serviceNo = originStop.serviceNo,
                        direction = originStop.direction,
                        originBusStopSequence = originStop.stopSequence % routeLength,
                        destinationBusStopSequence = destinationStop.stopSequence % routeLength,
                        sequence = uiState.value.segments.size
                    )
                )
                clearInput()
                setShowAddDialog(false)
                refreshList(afterNewAddition = true)
            }

        }

    }

    /**
     * Delete the specified segment from the Journey and the database.
     * Also updates the displayed segment list.
     *
     * @param journeySegmentInfo segment to be deleted
     */
    fun deleteBusJourney(journeySegmentInfo: JourneySegmentInfo) {
        viewModelScope.launch {
            userDataRepository.deleteJourneySegment(journeySegmentInfo)
            uiState.value.segments
                .forEach {
                    if (it.first.sequence > journeySegmentInfo.sequence) {
                        userDataRepository.deleteJourneySegment(it.first)
                        userDataRepository.insertJourneySegment(
                            it.first.copy(sequence = it.first.sequence - 1)
                        )
                    }
                }
            refreshList(afterNewAddition = false)
        }
    }

    /**
     * Swap the specified segment with the above segment in terms of sequence.
     * Also updates the displayed segment list.
     *
     * @param journeySegmentInfo bottom segment to be swapped
     */
    fun moveBusJourneyUp(journeySegmentInfo: JourneySegmentInfo) {
        if (journeySegmentInfo.sequence > 0) {
            viewModelScope.launch {
                swapBusJourneySequence(journeySegmentInfo, uiState.value.segments[journeySegmentInfo.sequence - 1].first)
            }
        }
    }

    /**
     * Swap the specified segment with the below segment in terms of sequence.
     * Also updates the displayed segment list
     *
     * @param journeySegmentInfo top segment to be swapped
     */
    fun moveBusJourneyDown(journeySegmentInfo: JourneySegmentInfo) {
        if (journeySegmentInfo.sequence < uiState.value.segments.size - 1) {
            viewModelScope.launch {
                swapBusJourneySequence(journeySegmentInfo, uiState.value.segments[journeySegmentInfo.sequence + 1].first)
            }
        }
    }

    /**
     * Swap the sequences of the two specified segments.
     * Also updates the displayed segment list.
     *
     * @param journeySegmentInfo1 segment one to be swapped
     * @param journeySegmentInfo2 segment two to be swapped
     */
    private suspend fun swapBusJourneySequence(
        journeySegmentInfo1: JourneySegmentInfo,
        journeySegmentInfo2: JourneySegmentInfo
    ) {
        userDataRepository.deleteJourneySegment(journeySegmentInfo1)
        userDataRepository.deleteJourneySegment(journeySegmentInfo2)
        val newBusJourney1 = journeySegmentInfo1.copy(sequence = journeySegmentInfo2.sequence)
        val newBusJourney2 = journeySegmentInfo2.copy(sequence = journeySegmentInfo1.sequence)
        userDataRepository.insertJourneySegment(newBusJourney1)
        userDataRepository.insertJourneySegment(newBusJourney2)
        refreshList(afterNewAddition = false)
    }

    /**
     * Set whether the bus icon for each bus arrival should be shown.
     *
     * @param showBusType True if the bus icon should be shown, false otherwise
     */
    fun setShowBusType(showBusType: Boolean) {
        _uiState.update {
            it.copy(showBusType = showBusType)
        }
    }

    /**
     * Set whether the first and last bus timings for each bus service should be shown.
     *
     * @param showFirstLastBus True if the first and last bus timings should be shown, false otherwise
     */
    fun setShowFirstLastBus(showFirstLastBus: Boolean) {
        _uiState.update {
            it.copy(showFirstLastBus = showFirstLastBus)
        }
    }

    /**
     * Set whether the destination bus stop, including the bus arrival data, should be shown.
     *
     * @param showDestinationBusArrivals True if the destination bus stop should be shown, false otherwise
     */
    fun setShowDestinationBusArrivals(showDestinationBusArrivals: Boolean) {
        _uiState.update {
            it.copy(showDestinationBusArrivals = showDestinationBusArrivals)
        }
    }

    /**
     * Clear all input values in Add Segment dialog
     */
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

    /**
     * Set the service number input and update the origin bus stop search list.
     *
     * @param serviceNo The service number input
     */
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

    /**
     * Set the direction of the bus service and update the origin bus stop search list.
     *
     * @param isDirectionTwo True for direction 2, false otherwise
     */
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

    /**
     * Set the origin bus stop input and update the destination bus stop search list.
     *
     * @param originStop The origin bus stop input
     */
    fun updateOriginStopInput(originStop: BusRouteInfoWithBusStopInfo) {
        viewModelScope.launch {
            _uiState.update {
                val busRoute =
                    busRepository.getBusServiceRoute(
                        serviceNo = originStop.busRouteInfo.serviceNo,
                        direction = originStop.busRouteInfo.direction
                    )

                it.copy(
                    originStopInput = originStop,
                    destinationStopInput = null,
                    destinationStopSearchResults =
                        truncateTillBusStop(
                            route = busRoute,
                            stopSequence = originStop.busRouteInfo.stopSequence,
                            adjustStopSequence = false
                        )
                )
            }
        }
    }

    /**
     * Set the destination bus stop input.
     *
     * @param destinationStop The destination bus stop input
     */
    fun updateDestinationStopInput(destinationStop: BusRouteInfoWithBusStopInfo) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    destinationStopInput = destinationStop
                )
            }
        }
    }

    /**
     * Set whether the origin bus stops search results should be expanded.
     *
     * @param expanded True if the search results should be expanded, false otherwise
     */
    fun setOriginStopSearchExpanded(expanded: Boolean) {
        _uiState.update {
            it.copy(originStopSearchExpanded = expanded)
        }
    }

    /**
     * Set whether the destination bus stops search results should be expanded.
     *
     * @param expanded True if the search results should be expanded, false otherwise
     */
    fun setDestinationStopSearchExpanded(expanded: Boolean) {
        _uiState.update {
            it.copy(destinationStopSearchExpanded = expanded)
        }
    }

}