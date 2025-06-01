package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun BusToMRTStationsUI(
    navController: NavHostController,
    latitude: Double,
    longitude: Double,
    stationCode: String
) {

    val busToMRTStationsViewModel: BusToMRTStationsViewModel = viewModel(
        factory = BusToMRTStationsViewModelFactory(
            latitude = latitude,
            longitude = longitude,
            stationCode = stationCode
        )
    )

    val busToMRTStationsUIState by busToMRTStationsViewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            val mrtStation = busToMRTStationsUIState.mrtStation

            if (mrtStation != null) {
                Text(
                    text = "${mrtStation.stationCode} ${mrtStation.stationName} MRT",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )

                if (busToMRTStationsUIState.routes.isNotEmpty()) {

                    if (!busToMRTStationsUIState.sortedByBusStop) {
                        Button(
                            onClick = { busToMRTStationsViewModel.sortByOriginBusStop() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Sort by origin bus stop",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    if (!busToMRTStationsUIState.sortedByNumberOfStops) {
                        Button(
                            onClick = { busToMRTStationsViewModel.sortByNumberOfStops() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Sort by number of stops",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    if (!busToMRTStationsUIState.sortedByWalkingDistance) {
                        Button(
                            onClick = { busToMRTStationsViewModel.sortByWalkingDistance() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Sort by walking distance",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 320.dp)
                    ) {
                        items(busToMRTStationsUIState.routes) { route ->
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
                Text(
                    text = "Loading",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

        }
    }

}