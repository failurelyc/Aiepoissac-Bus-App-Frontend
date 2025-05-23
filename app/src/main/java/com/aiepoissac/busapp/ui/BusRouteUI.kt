package com.aiepoissac.busapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo

@Composable
fun BusRouteUI(
    navController: NavHostController,
    serviceNo: String,
    direction: Int
) {

    val busRouteViewModel: BusRouteViewModel =
        viewModel(factory = BusRouteViewModelFactory(serviceNo = serviceNo, direction = direction))

    val busRouteUIState by busRouteViewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BusRouteList(
                navController = navController,
                uiState = busRouteUIState,
                revertButtonOnClick = { busRouteViewModel.setOriginalFirstBusStop() },
                showFirstLastBusButtonOnClick = { busRouteViewModel.toggleShowFirstLastBusToTrue()})
        }
    }
}

@Composable
fun BusRouteList(
    navController: NavHostController,
    uiState: BusRouteUIState,
    revertButtonOnClick: () -> Unit,
    showFirstLastBusButtonOnClick: () -> Unit
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

            if (uiState.truncated) {
                Text(
                    text = "Bus Stop ${data.first().busStopInfo.busStopCode} ${data.first().busStopInfo.description}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Text(
                    text = "Click the left card to view the route from there to the terminus or looping point",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Row {
            Button(
                onClick = showFirstLastBusButtonOnClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "${if (uiState.showFirstLastBus) "Hide" else "Show"} timings",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            if (uiState.truncated) {
                Button(
                    onClick = revertButtonOnClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "See full bus route",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        val gridState = rememberLazyGridState()

        LaunchedEffect(data) {
            gridState.scrollToItem(0)
        }

        LazyVerticalGrid(
            modifier = Modifier,
            state = gridState,
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {
            items(data) { route ->
                BusRouteInformation(navController = navController, data = route)
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
fun BusRouteInformation(
    navController: NavHostController,
    data: BusRouteInfoWithBusStopInfo
) {
    val busRouteViewModel: BusRouteViewModel = viewModel()

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            onClick = {
                if (!busRouteViewModel.isTruncatedRoute() || !busRouteViewModel.isLoop()) {
                    busRouteViewModel.setFirstBusStop(data.busRouteInfo.stopSequence)
                } else {
                    Toast.makeText(
                        BusApplication.instance,
                        "Revert to full route first", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(
                text = data.busRouteInfo.stopSequence.toString(),
                fontSize = if (busRouteViewModel.getShowFirstLastBus()) 24.sp else 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = String.format("%.1f km", data.busRouteInfo.distance),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Card(
            modifier = Modifier.weight(4f),
            onClick = { navigateToBusArrival(
                navController = navController,
                busStopInput = data.busRouteInfo.busStopCode
            ) }
        ) {

            Text(
                text = "${data.busRouteInfo.busStopCode} ${data.busStopInfo.description}",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Text(
                text = data.busStopInfo.roadName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            if (busRouteViewModel.getShowFirstLastBus()) {
                Text(
                    text = "WEEKDAY: ${data.busRouteInfo.wdFirstBus} to ${data.busRouteInfo.wdLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "SATURDAY: ${data.busRouteInfo.satFirstBus} to ${data.busRouteInfo.satLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "SUNDAY: ${data.busRouteInfo.sunFirstBus} to ${data.busRouteInfo.sunLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}