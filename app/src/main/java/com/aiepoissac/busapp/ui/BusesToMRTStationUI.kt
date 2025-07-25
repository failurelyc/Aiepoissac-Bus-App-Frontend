package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusToMRTStationsUI(
    navController: NavHostController,
    latitude: Double,
    longitude: Double,
    stationCode: String
) {

    val viewModel: BusToMRTStationsViewModel =
        viewModel(
            factory = BusToMRTStationsViewModelFactory(
                latitude = latitude,
                longitude = longitude,
                stationCode = stationCode
            )
        )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Card(
                        onClick = viewModel::sortByOriginBusStop,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Origin bus stop",
                            color = if (uiState.sortedByBusStop) Color.Blue else Color.Unspecified,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Card(
                        onClick = viewModel::sortByNumberOfStops,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Number of stops",
                            color = if (uiState.sortedByNumberOfStops) Color.Blue else Color.Unspecified,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Card(
                        onClick = viewModel::sortByWalkingDistance,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Walking distance",
                            color = if (uiState.sortedByWalkingDistance) Color.Blue else Color.Unspecified,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }
        }
    ) { innerPadding ->

        if (uiState.showAddSavedJourneyDialog) {
            TextFieldDialog(
                value = uiState.descriptionInput,
                title = "Add journey",
                label = "New description",
                onValueChange = viewModel::updateDescriptionInput,
                onDismissRequest = viewModel::toggleShowAddSavedJourneyDialog,
                onConfirmRequest = {
                    viewModel.addSavedJourney()
                    navigateToSavedJourneys(navController = navController)
                }
            )
        }

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            val mrtStation = uiState.mrtStation

            if (mrtStation != null && !uiState.searchingForBusServices) {
                Text(
                    text = "${mrtStation.second.stationCode} ${mrtStation.second.stationName} " +
                            "${mrtStation.second.type} (${mrtStation.first}m)",
                    textAlign = TextAlign.Left,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                )

                TimeSettings(
                    showOnlyOperatingBusServices = uiState.showOnlyOperatingBusServices,
                    showOnlyOperatingBusServicesOnCheckedChange = viewModel::setShowOnlyOperatingBusServices,
                    showTimeDial = uiState.showTimeDial,
                    updateTime = viewModel::updateTime,
                    setShowTimeDial = viewModel::setShowTimeDial,
                    currentDayOfWeek = uiState.dayOfWeek,
                    setDayOfWeek = viewModel::setDayOfWeek,
                    currentTime = uiState.currentTime
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::toggleShowAddSavedJourneyDialog
                ) {
                    Text(
                        text = "Save all to new Journey (max 10)"
                    )
                }

                if (uiState.routes.isNotEmpty()) {

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 320.dp)
                    ) {
                        items(uiState.routes) { route ->
                            val start = route.first.second
                            val end = route.second.second

                            Card(
                                modifier = Modifier.padding(8.dp),
                                onClick = { navigateToBusRouteInformation(
                                    navController = navController,
                                    serviceNo = start.busRouteInfo.serviceNo,
                                    direction = start.busRouteInfo.direction,
                                    stopSequence = start.busRouteInfo.stopSequence
                                ) }
                            ) {

                                Text(
                                    text = "${start.busRouteInfo.serviceNo} ${end.busRouteInfo.stopSequence - start.busRouteInfo.stopSequence} stops",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = "${start.busStopInfo.busStopCode} ${start.busStopInfo.description} (${route.first.first}m)",
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = "${end.busStopInfo.busStopCode} ${end.busStopInfo.description} (${route.second.first}m)",
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No bus services nearby to this station" ,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            } else {
                LoadingScreen(description = "Searching for bus services")
            }

        }
    }

}