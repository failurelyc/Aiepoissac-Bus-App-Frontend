package com.aiepoissac.busapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun NearbyUI(
    navController: NavHostController,
    latitude: Double,
    longitude: Double,
    isLiveLocation: Boolean
) {

    val nearbyViewModel: NearbyViewModel = viewModel(
        factory = NearbyViewModelFactory(
            latitude = latitude,
            longitude = longitude,
            isLiveLocation = isLiveLocation
        )
    )

    val nearbyUIState by nearbyViewModel.uiState.collectAsState()

    RequestLocationPermission {
        nearbyViewModel.updateLiveLocation()
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nearbyViewModel.toggleFreezeLocation()
                }
            ) {
                Icon(
                    if (nearbyUIState.isLiveLocation) Icons.Filled.LocationOn else Icons.Filled.LocationOff,
                    contentDescription = "Toggle live location button"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (nearbyUIState.mrtStationList.isNotEmpty()) {
                MRTStationList(
                    navController = navController,
                    uiState = nearbyUIState,
                    modifier = Modifier.weight(1f)
                )
                BusStopList(
                    navController = navController,
                    uiState = nearbyUIState,
                    modifier = Modifier.weight(2f)
                )
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
                    modifier = Modifier.width(64.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MRTStationList(
    navController: NavHostController,
    uiState: NearbyUIState,
    modifier: Modifier
) {

    val data = uiState.mrtStationList

    if (data.isNotEmpty()) {
        Text(
            text = "Nearby MRT Stations",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )

        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(minSize = 160.dp)
        ) {
            items(data) { mrtStation ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        navigateToBusToMRTStations(
                            navController = navController,
                            latitude = uiState.point.getCoordinates().first,
                            longitude = uiState.point.getCoordinates().second,
                            stationCode = mrtStation.second.stationCode
                        )
                    }
                ) {
                    Text(
                        text = "${mrtStation.second.stationCode} ${mrtStation.second.stationName} (${mrtStation.first}m)",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                }

            }
        }
    } else {
        Text(
            text = "No nearby MRT Stations",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    }

}

@Composable
private fun BusStopList(
    navController: NavHostController,
    uiState: NearbyUIState,
    modifier: Modifier
) {

    val data = uiState.busStopList


    if (data.isNotEmpty()) {
        Text(
            text = "Nearby Bus Stops",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )

        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {
            items(data) { busStop ->
                val busStopInfo = busStop.second
                Card(
                    onClick = { navigateToBusArrival(
                        navController = navController,
                        busStopInput = busStopInfo.busStopInfo.busStopCode) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "${busStopInfo.busStopInfo.busStopCode} ${busStopInfo.busStopInfo.description}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "${busStopInfo.busStopInfo.roadName} (${busStop.first}m)",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = busStopInfo.busRoutesInfo
                            .map{ it.serviceNo }
                            .distinct()
                            .joinToString(", ")
                        ,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

            }
        }
    } else {
        Text(
            text = "No nearby Bus Stops",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    }

}

private fun navigateToBusToMRTStations(
    navController: NavHostController,
    latitude: Double = 1.290270,
    longitude: Double = 103.851959,
    stationCode: String,
) {
    navController.navigate(
        Pages.BusesToMRTStation.with3Text(
            text1 = latitude.toString(),
            text2 = longitude.toString(),
            text3 = stationCode
        )
    )
}

@Composable
fun RequestLocationPermission(onGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted()
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            onGranted()
        }
    }
}
