package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.isLoop
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun BusRouteUI(
    navController: NavHostController,
    serviceNo: String,
    direction: Int,
    stopSequence: Int
) {

    val busRouteViewModel: BusRouteViewModel =
        viewModel(
            factory = BusRouteViewModelFactory(
                serviceNo = serviceNo,
                direction = direction,
                stopSequence = stopSequence
            )
        )

    val busRouteUIState by busRouteViewModel.uiState.collectAsState()

    val gridState = rememberLazyGridState()

    RequestLocationPermission {
        busRouteViewModel.updateLiveLocation()
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    MainScope().launch {
                        gridState.scrollToItem(0)
                    }
                }
            ) {
                Icon(
                    Icons.Filled.VerticalAlignTop,
                    contentDescription = "Speed"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BusRouteList(
                navController = navController,
                uiState = busRouteUIState,
                revertButtonOnClick = {
                    busRouteViewModel.setOriginalFirstBusStop()
                    MainScope().launch {
                        gridState.scrollToItem(0)
                    }
                                      },
                toggleLiveLocationOnClick = busRouteViewModel::toggleFreezeLocation,
                showFirstLastBusButtonOnClick = busRouteViewModel::toggleShowFirstLastBusToTrue,
                showRouteFromLoopingPointOnClick = {
                    busRouteViewModel.setLoopingPointAsFirstBusStop()
                    MainScope().launch {
                        gridState.scrollToItem(0)
                    }
                                                   },
                setFirstBusStop = busRouteViewModel::setFirstBusStop,
                switchDirection = { busRouteViewModel.toggleDirection() },
                gridState = gridState
            )
        }
    }
}

@Composable
private fun BusRouteList(
    navController: NavHostController,
    uiState: BusRouteUIState,
    revertButtonOnClick: () -> Unit,
    toggleLiveLocationOnClick: () -> Unit,
    showFirstLastBusButtonOnClick: () -> Unit,
    showRouteFromLoopingPointOnClick: () -> Unit,
    setFirstBusStop: (Int) -> Unit,
    switchDirection: () -> Unit,
    gridState: LazyGridState
) {

    val data = uiState.busRoute
    val busServiceInfo = uiState.busServiceInfo
    val configuration = LocalConfiguration.current

    if (busServiceInfo != null) {
        if (configuration.orientation == 1) {

            Text(
                text = "${busServiceInfo.operator} ${busServiceInfo.category} ${busServiceInfo.serviceNo}",
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            if (!busServiceInfo.isLoop()) {
                Text(
                    text = "Direction: ${busServiceInfo.direction}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Text(
                    text = "Loop At: ${busServiceInfo.loopDesc}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (uiState.isLiveLocation) Icons.Filled.LocationOn else Icons.Filled.LocationOff,
                    contentDescription = "location status",
                    modifier = Modifier.weight(0.5f)
                )

                Switch(
                    checked = uiState.isLiveLocation,
                    onCheckedChange = { toggleLiveLocationOnClick() },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Filled.AccessTime,
                    contentDescription = "First/Last bus timing",
                    modifier = Modifier.weight(0.5f)
                )

                Switch(
                    checked = uiState.showFirstLastBus,
                    onCheckedChange = { showFirstLastBusButtonOnClick() },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Filled.Speed,
                    contentDescription = "Speed",
                    modifier = Modifier.weight(0.5f)
                )

                if (uiState.isLiveLocation) {
                    Text(
                        text = "${uiState.currentSpeed}km/h",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = "-km/h",
                        modifier = Modifier.weight(1f)
                    )
                }

            }

            if (uiState.truncated) {
                Text(
                    text = "Bus Stop ${data.first().second.busStopInfo.busStopCode} ${data.first().second.busStopInfo.description}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Text(
                    text = "Click the left card to view the route from there to the terminus or looping point",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Row (
            modifier = Modifier.padding(4.dp)
        ) {
            if (uiState.truncated || uiState.truncatedAfterLoopingPoint) {
                Button(
                    onClick = revertButtonOnClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Full route",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            if ((isLoop(uiState.originalBusRoute) &&
                        ((!uiState.truncated && !uiState.truncatedAfterLoopingPoint) ||
                                uiState.truncated))) {
                Button(
                    onClick = showRouteFromLoopingPointOnClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "After loop",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

            }

            if (!isLoop(uiState.originalBusRoute)) {
                Button(
                    onClick = switchDirection,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Filled.SwapHoriz,
                        contentDescription = "Speed",
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }

        LazyVerticalGrid(
            modifier = Modifier,
            state = gridState,
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {
            items(data) { busRoute ->
                BusRouteInformation(
                    navController = navController,
                    data = busRoute,
                    uiState = uiState,
                    gridState = gridState,
                    setFirstBusStop = setFirstBusStop
                )
            }
        }
    } else {
        Text(
            text = "No such bus service.",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    }

}

@Composable
private fun BusRouteInformation(
    navController: NavHostController,
    data: Pair<Int, BusRouteInfoWithBusStopInfo>,
    uiState: BusRouteUIState,
    gridState: LazyGridState,
    setFirstBusStop: (Int) -> Unit,

) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            onClick = {
                setFirstBusStop(data.second.busRouteInfo.stopSequence)
                MainScope().launch {
                    gridState.scrollToItem(0)
                }
            }
        ) {
            Text(
                text = data.second.busRouteInfo.stopSequence.toString(),
                fontSize = if (uiState.showFirstLastBus) 24.sp else 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = String.format("%.1f km", data.second.busRouteInfo.distance),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Card(
            modifier = Modifier.weight(4f),
            onClick = { navigateToBusArrival(
                navController = navController,
                busStopInput = data.second.busRouteInfo.busStopCode
            ) }
        ) {

            val busRouteInfo = data.second.busRouteInfo
            val busStopInfo = data.second.busStopInfo

            Text(
                text = "${busRouteInfo.busStopCode} ${busStopInfo.description}",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Text(
                text = "${busStopInfo.roadName} (${if (uiState.isLiveLocation) data.first else "-"}m)",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            if (uiState.showFirstLastBus) {
                Text(
                    text = "WEEKDAY: ${busRouteInfo.wdFirstBus} to ${busRouteInfo.wdLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "SATURDAY: ${busRouteInfo.satFirstBus} to ${busRouteInfo.satLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "SUNDAY: ${busRouteInfo.sunFirstBus} to ${busRouteInfo.sunLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}