package com.aiepoissac.busapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.mrtstation.RealTrainServiceAlertsGetter
import com.aiepoissac.busapp.data.mrtstation.TrainServiceAlertsGetter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime

/**
 * This class is the View Model for the Train Service Alerts page
 *
 * @param trainServiceAlertsGetter The source of the Train Service Alerts and Facilities Maintenance data
 * @param busRepository The repository of the bus data
 */
class TrainServiceAlertViewModel(
    private val trainServiceAlertsGetter: TrainServiceAlertsGetter = RealTrainServiceAlertsGetter(),
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainServiceAlertUIState())
    val uiState = _uiState.asStateFlow()

    private var lastTimeRefreshPressed: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    init {
        refresh()
    }

    /**
     * Refresh the Train Service Alerts and Facilities Maintenance data
     */
    fun refresh() {
        val threshold = 60
        val currentTime = LocalDateTime.now()
        val difference = Duration.between(lastTimeRefreshPressed, currentTime).seconds

        if (difference > threshold) {
            viewModelScope.launch {
                try {
                    val facilitiesMaintenance = trainServiceAlertsGetter
                        .getFacilitiesMaintenance()
                        .value
                        .map {
                            Pair(
                                first = busRepository.getMRTStation(it.stationCode),
                                second = it
                            )
                        }
                    val trainServiceAlerts = trainServiceAlertsGetter.getTrainServiceAlerts()
                    lastTimeRefreshPressed = currentTime
                    _uiState.update { trainServiceAlertUIState ->
                        trainServiceAlertUIState.copy(
                            facilitiesMaintenance = facilitiesMaintenance,
                            trainServiceAlerts = trainServiceAlerts,
                            affectedSegmentsWithMRTStationList = trainServiceAlerts
                                .value.affectedSegments
                                .map {
                                    Pair(
                                        first = it,
                                        second = it.getAffectedStations(busRepository)
                                    )
                                }
                        )
                    }
                } catch (e: IOException) {
                    lastTimeRefreshPressed = LocalDateTime.MIN
                    _uiState.update {
                        it.copy(
                            facilitiesMaintenance = null,
                            trainServiceAlerts = null,
                            affectedSegmentsWithMRTStationList = null
                        )
                    }
                }
            }
        }

    }

    /**
     * Toggle whether facilities maintenance should be shown.
     */
    fun toggleShowFacilitiesMaintenance() {
        _uiState.update {
            it.copy(showFacilitiesMaintenance = !uiState.value.showFacilitiesMaintenance)
        }
    }

    /**
     * Toggle whether train service alerts should be shown.
     */
    fun toggleShowTrainServiceAlerts() {
        _uiState.update {
            it.copy(showTrainServiceAlerts = !uiState.value.showTrainServiceAlerts)
        }
    }

}