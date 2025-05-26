package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun NearbyUI(
    navController: NavHostController,
    latitude: Double,
    longitude: Double
) {

    val nearbyViewModel: NearbyViewModel = viewModel(
        factory = NearbyViewModelFactory(
            latitude = latitude,
            longitude = longitude
        )
    )

    val nearbyUIState by nearbyViewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BusStopList(
                navController = navController,
                uiState = nearbyUIState
            )
        }

    }

}

@Composable
private fun BusStopList(
    navController: NavHostController,
    uiState: NearbyUIState
) {

    val configuration = LocalConfiguration.current
    val data = uiState.busStopList

    if (configuration.orientation == 1) {
        Text(
            text = "Nearby Bus Stops",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )


    }

    LazyVerticalGrid(
        modifier = Modifier,
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

}
