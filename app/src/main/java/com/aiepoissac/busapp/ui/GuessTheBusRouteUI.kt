package com.aiepoissac.busapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo
import com.aiepoissac.busapp.ui.theme.GreenDark
import com.aiepoissac.busapp.ui.theme.GreenLight
import com.aiepoissac.busapp.ui.theme.RedDark
import com.aiepoissac.busapp.ui.theme.RedLight
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import kotlin.math.roundToInt

@Composable
fun GuessTheBusRouteUI(navController: NavHostController) {

    val guessTheBusRouteViewModel: GuessTheBusRouteViewModel = viewModel()
    val guessTheBusRouteUIState by guessTheBusRouteViewModel.uiState.collectAsState()

    val cameraPositionState by guessTheBusRouteViewModel.cameraPositionState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            if (guessTheBusRouteUIState.difficulty <= 0) {
                ValueSelector(
                    valueName = "Difficulty",
                    maximumValue = 10,
                    currentValue = guessTheBusRouteUIState.difficultyInput,
                    setValue = guessTheBusRouteViewModel::updateDifficultyInput,
                    submitValue = guessTheBusRouteViewModel::setDifficulty,
                    descriptions =
                        listOf(
                            "",
                            "Non loop parent services only, origin and destination shown, 2 choices, 128s time limit",
                            "Non loop parent services only, origin and destination shown, 4 choices, 128s time limit",
                            "Non loop parent services only, 6 choices, 128s time limit",
                            "Non loop services only, 8 choices, 128s time limit",
                            "Non loop services only, 10 choices, 128s time limit",
                            "All services, 12 choices, 64s time limit",
                            "All services, 14 choices, 32s time limit",
                            "All services, 16 choices, 16s time limit",
                            "All services, 18 choices, 8s time limit",
                            "Impossible mode"
                        ),
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = "How to play:\n" +
                            "1. You will be given a bus route on a map, and a list of bus services. " +
                            "Choose the bus service that best fits the route shown within the time limit.\n" +
                            "2. You may save your streak at the end of each question. " +
                            "However, your saved streak will be deleted once you reload the game. " +
                            "You will have to attempt another question before being able to save your streak again.\n" +
                            "3. You will lose your streak if you quit before answering the question shown, " +
                            "you answer the question wrongly, or the timer runs out.",
                    modifier = Modifier.padding(4.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            } else {

                GuessTheBusRouteQuestion(
                    uiState = guessTheBusRouteUIState,
                    cameraPositionState = cameraPositionState,
                    navController = navController,
                    selectChoice = guessTheBusRouteViewModel::setChoice,
                    validateChoice = guessTheBusRouteViewModel::validateChoice,
                    generateQuestion = guessTheBusRouteViewModel::generateNewQuestion,
                    exit = {
                        guessTheBusRouteViewModel.recordCurrentStreak()
                        navigateToHomePage(navController)
                    },
                    modifier = Modifier.padding(4.dp)
                )

            }
        }
    }
}

@Composable
private fun ValueSelector(
    valueName: String,
    maximumValue: Int,
    currentValue: Float,
    setValue: (Float) -> Unit,
    submitValue: (Int) -> Unit,
    descriptions: List<String>,
    modifier: Modifier
) {

    Column(
        modifier = modifier
    ) {
        Text(
            text = "Select $valueName",
            fontSize = 24.sp
        )

        Slider(
            value = currentValue,
            onValueChange = setValue,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = maximumValue - 1,
            valueRange = 0f..maximumValue.toFloat()
        )
        Text(
            text = "Selected $valueName: ${currentValue.roundToInt()} (${descriptions[currentValue.roundToInt()]})"
        )

        Button(
            onClick = { submitValue(currentValue.roundToInt()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Confirm")
        }
    }
}

@Composable
private fun GuessTheBusRouteQuestion(
    uiState: GuessTheBusRouteUIState,
    cameraPositionState: CameraPositionState,
    navController: NavHostController,
    selectChoice: (BusServiceInfoWithBusStopInfo) -> Unit,
    validateChoice: () -> Unit,
    generateQuestion: () -> Unit,
    exit: () -> Unit,
    modifier: Modifier
) {

    val configuration = LocalConfiguration.current

    Column(
        modifier = modifier
    ) {

        Text(
            text = "Score: ${uiState.score}/${uiState.count} " +
                    "Time left: ${if (uiState.timeLeft == Long.MAX_VALUE) "-" else uiState.timeLeft} seconds"
        )

        Text(
            text = "Current streak: ${uiState.streak} Highest streak: ${uiState.highestStreak}"
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(
                minSize = if (uiState.difficulty < 3 || uiState.hideMap) 320.dp else 120.dp
            ),
            modifier = Modifier.heightIn(max = if (configuration.orientation == 1) 240.dp else 80.dp)
        ) {
            items(uiState.choices) {

                Surface(
                    color =
                        if (uiState.showAnswer && uiState.correctChoice != it && uiState.selected == it) {
                            if (!isSystemInDarkTheme()) RedLight else RedDark
                        } else if (uiState.showAnswer && uiState.correctChoice == it) {
                            if (!isSystemInDarkTheme()) GreenLight else GreenDark
                        } else if (uiState.selected == it) {
                            if (!isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                        } else {
                            Color.Unspecified
                        },
                    modifier = Modifier.clickable {
                        if (!uiState.showAnswer) {
                            selectChoice(it)
                        } else {
                            navigateToBusRouteInformation(
                                navController = navController,
                                serviceNo = it.busServiceInfo.serviceNo,
                                direction = it.busServiceInfo.direction
                            )
                        }
                    }
                ) {
                    Column {
                        Text(
                            text = "${it.busServiceInfo.operator} ${it.busServiceInfo.serviceNo}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(2.dp)
                        )

                        if (uiState.difficulty < 3 || uiState.hideMap) {
                            Text(
                                text = "${it.originBusStopInfo.description} to ${it.destinationBusStopInfo.description}",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

            }
        }

        if (uiState.showAnswer) {

            if (configuration.orientation == 1) {
                Text(
                    text = "Click on a bus service to view its route.",
                )
            }

            Button(
                onClick = generateQuestion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Next question")
            }

            Button(
                onClick = exit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save streak and exit")
            }

        } else {

            if (configuration.orientation == 1) {
                Text(
                    text = "Select the bus service and submit.",
                )
            }

            Button(
                onClick = validateChoice,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit answer")
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxWidth(),
            cameraPositionState = cameraPositionState,
            contentDescription = "Map of bus route",
            properties = if (uiState.hideMap) { MapProperties(mapType = MapType.NONE) } else DefaultMapProperties
        ) {

            uiState.busRoute.forEach {
                val busStopInfo = it.busStopInfo
                Circle(
                    center = LatLng(busStopInfo.latitude,busStopInfo.longitude),
                    fillColor = Color.Red,
                    strokeColor = Color.Red
                )
            }

        }
    }

}