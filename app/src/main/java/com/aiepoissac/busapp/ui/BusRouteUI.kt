package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.aiepoissac.busapp.data.businfo.BusRouteInfo

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
            BusRouteList(navController = navController, uiState = busRouteUIState)
        }
    }
}

@Composable
fun BusRouteList(
    navController: NavHostController,
    uiState: BusRouteUIState
) {

    val data: List<BusRouteInfo> = uiState.busRoute

    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.Adaptive(minSize = 320.dp)
    ) {
        items(data) { route ->
            BusRouteInformation(navController = navController, data = route)
        }
    }

}

@Composable
fun BusRouteInformation(
    navController: NavHostController,
    data: BusRouteInfo
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = data.stopSequence.toString(),
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = data.distance.toString() + "km",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Card(
            modifier = Modifier.weight(4f),
            onClick = { navigateToBusArrival(
                navController = navController,
                busStopInput = data.busStopCode
            ) }) {
            Text(
                text = data.busStopCode,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            )

            Text(
                text = "WEEKDAY: ${data.wdFirstBus} to ${data.wdLastBus}",
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = "SATURDAY: ${data.satFirstBus} to ${data.satLastBus}",
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = "SUNDAY: ${data.sunFirstBus} to ${data.sunLastBus}",
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

        }
    }
}