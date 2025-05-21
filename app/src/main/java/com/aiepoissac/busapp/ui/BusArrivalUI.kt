package com.aiepoissac.busapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.busarrival.BusStop

@Composable
fun BusArrivalUI(
    navController: NavHostController,
    busStopCodeInput: String = "") {


    val busArrivalViewModel: BusArrivalViewModel =
        viewModel(factory = BusArrivalViewModelFactory(busStopCodeInput))

    val busArrivalUIState by busArrivalViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current

    Scaffold (
        bottomBar = {
            if (configuration.orientation == 1) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = busArrivalViewModel.getBusArrivalColor(
                                s = "SEA",
                                darkMode = isSystemInDarkTheme()),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Seats Available",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Surface(
                            color = busArrivalViewModel.getBusArrivalColor(
                                s = "SDA",
                                darkMode = isSystemInDarkTheme()),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Standing Available",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Surface(
                            color = busArrivalViewModel.getBusArrivalColor(
                                s = "LSD",
                                darkMode = isSystemInDarkTheme()),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Standing Limited",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Surface(
                            color = busArrivalViewModel.getBusArrivalColor(
                                darkMode = isSystemInDarkTheme()),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Not begun service",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    ) {
        innerPadding ->
            Column (
                modifier = Modifier.padding(innerPadding)

            ) {
                BusStopCodeForBusArrival(
                    onBusStopCodeChanged = { busArrivalViewModel.updateBusStopCodeInput(it) },
                    onKeyBoardDone = { busArrivalViewModel.updateBusStop() },
                    busStopCodeInput = busArrivalViewModel.busStopCodeInput,
                    isError = busArrivalUIState.busArrivalData == null
                )
                BusArrivalsList(
                    uiState = busArrivalUIState,
                    onRefresh = { busArrivalViewModel.refreshBusArrival() },
                    navController = navController)
            }
    }

}

@Composable
private fun BusStopCodeForBusArrival(
    onBusStopCodeChanged: (String) -> Unit,
    onKeyBoardDone: () -> Unit,
    busStopCodeInput: String,
    isError: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = busStopCodeInput,
            singleLine = true,
            onValueChange = onBusStopCodeChanged,
            label = { Text("Bus Stop Code") },
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = {onKeyBoardDone()}),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusArrivalsList(
    uiState: BusArrivalUIState,
    onRefresh: () -> Unit,
    navController: NavHostController) {

    val data: BusStop? = uiState.busArrivalData
    val configuration = LocalConfiguration.current

    if (uiState.networkIssue) {
        Text(
            text = "Error in getting data. \nCheck your internet connection!",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    } else if (data != null && data.services.isNotEmpty()) {
        if (configuration.orientation == 1) {
            Text(
                fontSize = 24.sp,
                text = "Bus Stop ${data.busStopCode}",
                modifier = Modifier.padding(8.dp)
            )
        }
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
        ) {
            LazyVerticalGrid (
                modifier = Modifier,
                columns = GridCells.Adaptive(minSize = 320.dp)
            ) {
                items(data.services) { service ->
                    BusArrivalsLayout(
                        data = service,
                        navController = navController)
                }
            }
        }
    } else {
        Text(
            text = "No bus services currently operating at this bus stop.",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    }


}

@Composable
private fun BusArrivalsLayout(
    navController: NavHostController,
    data: BusService) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
            .weight(1f)
            .clickable {
                navigateToBusServiceInformation(
                    navController = navController,
                    busServiceInput = data.serviceNo
                )
            }
        ) {

            Text(
                text = data.serviceNo,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "To: ${data.nextBus.destinationCode}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = data.operator,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Card(modifier = Modifier.weight(2f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                BusArrivalLayout(data = data.nextBus, modifier = Modifier.weight(1f))
                BusArrivalLayout(data = data.nextBus2, modifier = Modifier.weight(1f))
                BusArrivalLayout(data = data.nextBus3, modifier = Modifier.weight(1f))
            }
        }

    }
}

@Composable
private fun BusArrivalLayout(data: Bus, modifier: Modifier = Modifier) {

    val busArrivalViewModel: BusArrivalViewModel = viewModel()

    Surface(
        color = busArrivalViewModel.getBusArrivalColor(data, isSystemInDarkTheme()),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Text(
//                text = data.type,
//                color = Color.Black,
//                fontSize = 24.sp,
//                modifier = Modifier.padding(4.dp)
//            )

            Image(
                painter = painterResource(id = busArrivalViewModel.busTypeToPicture(data)),
                contentDescription = "Bus Image",
                modifier = Modifier.heightIn(max = 80.dp).widthIn(max = 80.dp)
            )

            if (data.isValid()) {
                Text(
                    text = "${data.getDuration()} min",
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                if (data.visitNumber != "1") {
                    Text(
                        text = "(Visit: ${data.visitNumber})",
                        color = Color.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            } else {
                Text(
                    text = "-",
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

        }
    }
}

