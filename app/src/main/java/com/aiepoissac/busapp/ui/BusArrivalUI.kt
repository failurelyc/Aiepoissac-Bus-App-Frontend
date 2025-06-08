package com.aiepoissac.busapp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MobiledataOff
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NoTransfer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo

@Composable
fun BusArrivalUI(
    navController: NavHostController,
    busStopCodeInput: String = ""
) {

    val busArrivalViewModel: BusArrivalViewModel =
        viewModel(factory = BusArrivalViewModelFactory(
            busStopCodeInput = busStopCodeInput))

    val busArrivalUIState by busArrivalViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current

    Scaffold (
        floatingActionButton = {
            if (!busArrivalUIState.showBusArrival) {
                FloatingActionButton(
                    onClick = { busArrivalViewModel.switchToOppositeBusStop() }
                ) {
                    Icon(
                        Icons.Filled.SwapVert,
                        contentDescription = "Opposite bus stop"
                    )
                }
            } else {
                FloatingActionButton(
                    onClick = { busArrivalViewModel.refreshBusArrival() }
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh Button")
                }
            }
        },

        bottomBar = {
            if (configuration.orientation == 1 && busArrivalUIState.showBusArrival) {
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
                                darkMode = isSystemInDarkTheme()
                            ),
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
                                darkMode = isSystemInDarkTheme()
                            ),
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
                                darkMode = isSystemInDarkTheme()
                            ),
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
                                darkMode = isSystemInDarkTheme()
                            ),
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
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.DepartureBoard,
                        contentDescription = "Bus Arrival Timings",
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = busArrivalUIState.showBusArrival,
                        onCheckedChange = { busArrivalViewModel.toggleShowBusArrival() }
                    )

                    Icon(
                        if (busArrivalUIState.hideBusType) Icons.Filled.NoTransfer else Icons.Filled.DirectionsBus,
                        contentDescription = "Toggle show bus type",
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = !busArrivalUIState.hideBusType,
                        onCheckedChange = { busArrivalViewModel.toggleHideBusType() }
                    )

                }

                if (busArrivalUIState.busStopInfo != null) {
                    Button(
                        onClick = { navigateToNearby(
                            navController = navController,
                            latitude = busArrivalUIState.busStopInfo!!.latitude,
                            longitude = busArrivalUIState.busStopInfo!!.longitude
                        ) },
                        modifier = Modifier.padding(4.dp).fillMaxWidth()
                    ) {
                        Row {
                            Icon(
                                Icons.Filled.NearMe,
                                contentDescription = "Nearby bus/MRT"
                            )

                            Text(
                                text = "Nearby bus/MRT",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                if (busArrivalUIState.showBusArrival) {
                    BusArrivalsList(
                        uiState = busArrivalUIState,
                        onRefresh = busArrivalViewModel::refreshBusArrival,
                        navController = navController
                    )
                } else {
                    if (configuration.orientation == 1) {
                        SearchBarWithSuggestions(
                            onQueryChange = busArrivalViewModel::updateBusStopCodeInput,
                            onSearch = { busArrivalViewModel.updateBusStop() },
                            query = busArrivalUIState.busStopCodeInput,
                            placeholder = "Bus stop code or name",
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Clear,
                                    contentDescription = "Clear input",
                                    modifier = Modifier.clickable {
                                        busArrivalViewModel.updateBusStopCodeInput(busStopCodeInput = "")
                                    }
                                )
                            },
                            expanded = busArrivalUIState.expanded,
                            onExpandedChange = busArrivalViewModel::setExpanded,
                            searchResults = busArrivalUIState.searchResult,
                            onItemClick = { busStop ->
                                busArrivalViewModel.updateBusStopCodeInput(busStop.busStopCode)
                                busArrivalViewModel.updateBusStop()
                            },
                            itemContent = { busStop ->
                                Text("${busStop.busStopCode} ${busStop.description}", modifier = Modifier.padding(8.dp))
                            }
                        )
                    }

                    BusStopInformation(
                        uiState = busArrivalUIState,
                        navController = navController
                    )
                }
            }
    }
}

@Composable
private fun BusStopInformation(
    uiState: BusArrivalUIState,
    navController: NavHostController
) {

    val data: List<BusRouteInfo> = uiState.busRoutes
    val busStopInfo: BusStopInfo? = uiState.busStopInfo

    if (busStopInfo == null) {
        ErrorMessage(
            text = "No such bus stop!",
            icon = Icons.Filled.NoTransfer
        )
    } else {

        Text(
            fontSize = 24.sp,
            text = "${busStopInfo.busStopCode} ${busStopInfo.description}",
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = busStopInfo.roadName,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        LazyVerticalGrid (
            modifier = Modifier,
            columns = GridCells.Adaptive(minSize = 80.dp)
        ) {
            items(data) { route ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = { navigateToBusRouteInformation(
                        navController = navController,
                        serviceNo = route.serviceNo,
                        direction = route.direction,
                        stopSequence = route.stopSequence
                    ) }
                ) {
                    Text(
                        text = route.serviceNo,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = route.operator,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Direction: ${route.direction}",
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusArrivalsList(
    uiState: BusArrivalUIState,
    onRefresh: () -> Unit,
    navController: NavHostController) {

    val data = uiState.busArrivalData
    val busStopInfo: BusStopInfo? = uiState.busStopInfo

    if (busStopInfo == null) {
        ErrorMessage(
            text = "No such bus stop!",
            icon = Icons.Filled.NoTransfer
        )
    } else {
        if (data == null) {
            ErrorMessage(
                text = "Data is currently unavailable.",
                icon = Icons.Filled.MobiledataOff
            )
        } else if (data.isNotEmpty()){
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
            ) {
                LazyVerticalGrid (
                    modifier = Modifier,
                    columns = GridCells.Adaptive(minSize = 320.dp)
                ) {
                    items(data) { service ->
                        BusArrivalsLayout(
                            data = service,
                            navController = navController,
                            hideBusType = uiState.hideBusType
                        )
                    }
                }
            }
        } else {
            ErrorMessage(
                text = "No bus services operating now",
                icon = Icons.Filled.NoTransfer
            )
        }
    }
}

@Composable
private fun BusArrivalsLayout(
    navController: NavHostController,
    data: Pair<Pair<BusStopInfo?, BusStopInfo?>, BusService>,
    hideBusType: Boolean = false
) {

    val busArrivalViewModel: BusArrivalViewModel = viewModel()
    val busService = data.second
    val originDestination = data.first

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            onClick = {
                val busRoute = busArrivalViewModel.getBusRoute(busService.serviceNo)
                if (busRoute != null) {
                    navigateToBusRouteInformation(
                        navController = navController,
                        serviceNo = busRoute.serviceNo,
                        direction = busRoute.direction,
                        stopSequence = busRoute.stopSequence
                    )
                } else {
                    Toast.makeText(
                        BusApplication.instance,
                        "View the bus route from bus stop details section",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        ) {

            Text(
                text = busService.serviceNo,
                fontSize = if (!hideBusType) 36.sp else 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = originDestination.second?.description ?: "an error occurred",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (!hideBusType) {

                if (busService.nextBus.isLoop()) {
                    Text(
                        text = "(loop)",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = busService.operator,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
        Card(modifier = Modifier.weight(2f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ) {
                BusArrivalLayout(
                    data = busService.nextBus,
                    modifier = Modifier.weight(1f),
                    hideBusType = hideBusType
                )
                BusArrivalLayout(
                    data = busService.nextBus2,
                    modifier = Modifier.weight(1f),
                    hideBusType = hideBusType
                )
                BusArrivalLayout(
                    data = busService.nextBus3,
                    modifier = Modifier.weight(1f),
                    hideBusType = hideBusType
                )
            }
        }

    }
}

@Composable
private fun BusArrivalLayout(
    data: Bus,
    modifier: Modifier = Modifier,
    hideBusType: Boolean = false
) {

    val busArrivalViewModel: BusArrivalViewModel = viewModel()

    Surface(
        color = busArrivalViewModel.getBusArrivalColor(data, isSystemInDarkTheme()),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!hideBusType) {
                Image(
                    painter = painterResource(id = busArrivalViewModel.busTypeToPicture(data)),
                    contentDescription = "Bus Image",
                    modifier = Modifier.heightIn(max = 80.dp).widthIn(max = 80.dp)
                )
            }

            if (data.isValid()) {
                Text(
                    text = "${data.getDuration()} min",
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = busArrivalViewModel.getDistance(data),
                    color = Color.Black,
                    fontSize = 10.sp,
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

@Composable
private fun ErrorMessage(
    text: String,
    icon: ImageVector
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
        modifier = Modifier.fillMaxWidth()
    )

    Icon(
        imageVector = icon,
        contentDescription = text,
        modifier = Modifier.fillMaxSize()
    )
}

