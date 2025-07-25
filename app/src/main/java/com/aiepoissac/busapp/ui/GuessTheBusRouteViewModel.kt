package com.aiepoissac.busapp.ui

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

class GuessTheBusRouteViewModel(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuessTheBusRouteUIState())
    val uiState: StateFlow<GuessTheBusRouteUIState> = _uiState.asStateFlow()

    private val _cameraPositionState = MutableStateFlow(
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(LatLng(1.290270, 103.851959), 12f)
        )
    )
    val cameraPositionState = _cameraPositionState.asStateFlow()

    fun updateDifficultyInput(difficultyInput: Float) {
        _uiState.update {
            it.copy(difficultyInput = difficultyInput)
        }
    }

    fun setDifficulty(difficulty: Int) {
        recordHighestStreak()
        _uiState.update {
            it.copy(
                difficulty = difficulty,
                score = 0,
                streak = 0,
                highestStreak = 0,
                hideMap = false
            )
        }

        if (difficulty > 9) {
            _uiState.update {
                it.copy(hideMap = true)
            }
        }
        getStreak()
        generateNewQuestion()
    }

    fun generateNewQuestion() {
        val difficulty = uiState.value.difficulty
        //1: Non loop parent services only, origin and destination shown, 2 choices, 128s time limit
        //2: Non loop parent services only, origin and destination shown, 4 choices, 128s time limit
        //3: Non loop parent services only, 6 choices, 128s time limit
        //4: Non loop services only, 8 choices, 128s time limit
        //5: Non loop services only, 10 choices, 128s time limit
        //6: All services, 12 choices, 64s time limit
        //7: All services, 14 choices, 32s time limit
        //8: All services, 16 choices, 16s time limit
        //9: All services, 18 choices, 8s time limit
        //10: All services, 20 choices, 8s time limit, origin and destination shown, no map
        viewModelScope.launch {
            val busServiceChoices = mutableListOf<BusServiceInfoWithBusStopInfo>()
            while (busServiceChoices.size < difficulty * 2) {
                val randomBusServiceInput = Random.nextInt(until = 1000).toString()
                if (busServiceChoices.none { it.busServiceInfo.serviceNo == randomBusServiceInput }) {
                    val busServices = busRepository.getBusService(randomBusServiceInput)
                    if (busServices.isNotEmpty()) {
                        if (difficulty < 4) {
                            val busService = busServices.first()
                            if (!busService.busServiceInfo.isLoop()) {
                                busServiceChoices.add(busService)
                            }
                        } else {
                            val busService = busServices[Random.nextInt(until = busServices.size)]
                            if (difficulty > 6 || !busService.busServiceInfo.isLoop()) {
                                busServiceChoices.add(busService)
                            }
                        }
                    }
                }
            }
            val correctBusService =
                busServiceChoices[Random.nextInt(until = busServiceChoices.size)]
            val busRoute =
                busRepository.getBusServiceRoute(
                    serviceNo = correctBusService.busServiceInfo.serviceNo,
                    direction = correctBusService.busServiceInfo.direction
                )
            val timeGenerated = LocalDateTime.now()

            _uiState.update {
                it.copy(
                    showAnswer = false,
                    choices = busServiceChoices.sortedBy { it.busServiceInfo.serviceNo },
                    correctChoice = correctBusService,
                    busRoute = busRoute,
                    timeLeft = Long.MAX_VALUE
                )
            }

            updateCameraPositionToFirstStop()

            val timeLeft = (2.0).pow(12 - difficulty).toInt().coerceIn(8, 128)
            while (uiState.value.timeLeft > 0 && !uiState.value.showAnswer) {
                _uiState.update {
                    it.copy(timeLeft = timeLeft - Duration.between(timeGenerated, LocalDateTime.now()).seconds)
                }
                delay(timeMillis = 100)
            }
            if (uiState.value.timeLeft == 0L) {
                validateChoice()
            }

        }

    }

    fun setChoice(busServiceInfoWithBusStopInfo: BusServiceInfoWithBusStopInfo) {
        if (!uiState.value.showAnswer) {
            _uiState.update {
                it.copy(selected = busServiceInfoWithBusStopInfo)
            }
        }
    }

    fun validateChoice() {
        val selected = uiState.value.selected
        val correctChoice = uiState.value.correctChoice
        val answeredOnTime = uiState.value.timeLeft > -1
        if (correctChoice != null && !uiState.value.showAnswer) {
            if (correctChoice == selected && answeredOnTime) {
                _uiState.update {
                    it.copy(
                        showAnswer = true,
                        streak = uiState.value.streak + 1,
                        score = uiState.value.score + 1,
                        count = uiState.value.count + 1,
                        timeLeft = -1
                    )
                }
            } else {
                recordHighestStreak()
                _uiState.update {
                    it.copy(
                        showAnswer = true,
                        streak = 0,
                        highestStreak = max(uiState.value.highestStreak, uiState.value.streak),
                        count = uiState.value.count + 1,
                        timeLeft = -1
                    )
                }
            }
        }
    }

    private fun updateCameraPositionToFirstStop() {
        if (uiState.value.busRoute.isNotEmpty()) {
            val busStopInfo = uiState.value.busRoute.first().busStopInfo
            updateCameraPosition(LatLng(busStopInfo.latitude, busStopInfo.longitude))
        }
    }

    private fun updateCameraPosition(target: LatLng, zoomIn: Boolean = false) {
        _cameraPositionState.update {
            CameraPositionState(
                position = CameraPosition(
                    target,
                    if (zoomIn) 25f else it.position.zoom,
                    it.position.tilt,
                    it.position.bearing
                )
            )
        }
    }

    private fun recordHighestStreak() {
        if (uiState.value.streak > uiState.value.highestStreak) {
            val sharedPreferences = BusApplication.instance
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit {
                putInt("GuessTheBusRouteStreak" + uiState.value.difficulty, uiState.value.streak)
            }
        }
    }

    private fun getStreak() {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        _uiState.update {
            it.copy(
                highestStreak =
                    sharedPreferences.getInt("GuessTheBusRouteStreak" + uiState.value.difficulty, 0),
                streak =
                    sharedPreferences.getInt("GuessTheBusRouteCurrentStreak" + uiState.value.difficulty, 0)
            )
        }
        sharedPreferences.edit {
            putInt("GuessTheBusRouteCurrentStreak" + uiState.value.difficulty, 0)
        }
    }

    fun recordCurrentStreak() {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putInt("GuessTheBusRouteCurrentStreak" + uiState.value.difficulty, uiState.value.streak)
        }
    }

}