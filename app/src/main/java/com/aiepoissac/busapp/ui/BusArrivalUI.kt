package com.aiepoissac.busapp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Download
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
import com.aiepoissac.busapp.data.HasCoordinates
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
        viewModel(
            factory = BusArrivalViewModelFactory(
                busStopCodeInput = busStopCodeInput
            )
        )

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
                            color = getBusArrivalColor(
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
                            color = getBusArrivalColor(
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
                            color = getBusArrivalColor(
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
                            color = getBusArrivalColor(
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
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(2.dp)
                ) {

                    IconTextSwitch(
                        icon = Icons.Filled.DepartureBoard,
                        text = "Arrivals",
                        checked = busArrivalUIState.showBusArrival,
                        onCheckedChange = busArrivalViewModel::setShowBusArrival,
                        modifier = Modifier.weight(1f)
                    )

                    if (busArrivalUIState.showBusArrival) {
                        IconTextSwitch(
                            icon = Icons.Filled.DirectionsBus,
                            text = "Bus type",
                            checked = busArrivalUIState.showBusType,
                            onCheckedChange = busArrivalViewModel::setShowBusType,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        IconTextSwitch(
                            icon = Icons.Filled.AccessTime,
                            text = "First/Last bus",
                            checked = busArrivalUIState.showFirstLastBus,
                            onCheckedChange = busArrivalViewModel::setShowFirstLastBus,
                            modifier = Modifier.weight(1f)
                        )
                    }

                }

                if (busArrivalUIState.busStopInfo != null) {
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { navigateToNearby(
                                navController = navController,
                                latitude = busArrivalUIState.busStopInfo!!.latitude,
                                longitude = busArrivalUIState.busStopInfo!!.longitude
                            ) },
                            modifier = Modifier.padding(4.dp).weight(1f)
                        ) {
                            Row {
                                Icon(
                                    Icons.Filled.NearMe,
                                    contentDescription = "Nearby bus/MRT"
                                )

                                Text(
                                    text = "Nearby",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }

                        Button(
                            onClick = busArrivalViewModel::openDirections,
                            modifier = Modifier.padding(4.dp).weight(1f)
                        ) {
                            Row {
                                Icon(
                                    Icons.Filled.Directions,
                                    contentDescription = "Directions"
                                )

                                Text(
                                    text = "Directions",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (busArrivalUIState.showBusArrival) {
                    BusArrivalsList(
                        uiState = busArrivalUIState,
                        onRefresh = busArrivalViewModel::refreshBusArrival,
                        getBusRoute = busArrivalViewModel::getBusRoute,
                        setShowBusArrival = busArrivalViewModel::setShowBusArrival,
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
fun IconTextSwitch(
    icon: ImageVector,
    text: String,
    showText: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.weight(1f)
                .align(Alignment.CenterVertically)
        )

        if (showText) {
            Text(
                text = text,
                modifier = Modifier.weight(2f)
                    .align(Alignment.CenterVertically)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.weight(1f)
                .align(Alignment.CenterVertically)
        )
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
            text = busStopInfo.toString(),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = busStopInfo.roadName,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        LazyVerticalGrid (
            modifier = Modifier,
            columns = GridCells.Adaptive(
                minSize = if (!uiState.showFirstLastBus) 80.dp else 320.dp
            )
        ) {
            items(data) { route ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navigateToBusRouteInformation(
                                navController = navController,
                                serviceNo = route.serviceNo,
                                direction = route.direction,
                                stopSequence = route.stopSequence
                            )
                        }
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

                    if (uiState.showFirstLastBus) {
                        Card(
                            modifier = Modifier.weight(4f)
                        ) {
                            Text(
                                text = "WEEKDAY: ${route.wdFirstBus} to ${route.wdLastBus}",
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )

                            Text(
                                text = "SATURDAY: ${route.satFirstBus} to ${route.satLastBus}",
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )

                            Text(
                                text = "SUNDAY: ${route.sunFirstBus} to ${route.sunLastBus}",
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )
                        }
                    }
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
    navController: NavHostController,
    getBusRoute: (String) -> BusRouteInfo?,
    setShowBusArrival: (Boolean) -> Unit,
) {

    val data = uiState.busArrivalData
    val busStopInfo: BusStopInfo? = uiState.busStopInfo

    if (busStopInfo == null) {
        ErrorMessage(
            text = "No such bus stop!",
            icon = Icons.Filled.NoTransfer
        )
    } else {
        if (uiState.connectionIssue) {
            ErrorMessage(
                text = "Bus arrival data is currently unavailable.",
                icon = Icons.Filled.MobiledataOff
            )
        } else if (data == null) {
            ErrorMessage(
                text = "Loading bus arrivals",
                icon = Icons.Filled.Download
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
                            busStopInfo = busStopInfo,
                            navController = navController,
                            showBusType = uiState.showBusType,
                            getBusRoute = getBusRoute,
                            setShowBusArrival = setShowBusArrival
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
    busStopInfo: BusStopInfo,
    data: Pair<Pair<BusStopInfo?, BusStopInfo?>, BusService>,
    getBusRoute: (String) -> BusRouteInfo?,
    setShowBusArrival: (Boolean) -> Unit,
    showBusType: Boolean = true
) {

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
                val busRoute = getBusRoute(busService.serviceNo)
                if (busRoute != null) {
                    navigateToBusRouteInformation(
                        navController = navController,
                        serviceNo = busRoute.serviceNo,
                        direction = busRoute.direction,
                        stopSequence = busRoute.stopSequence
                    )
                } else {
                    setShowBusArrival(false)
                    Toast.makeText(
                        BusApplication.instance,
                        "Click on the bus service here",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        ) {

            Text(
                text = busService.serviceNo,
                fontSize = if (showBusType) 36.sp else 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = originDestination.second?.description ?: "an error occurred",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (showBusType) {

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
        Card(
            modifier = Modifier.weight(2f),
            onClick = {
                val busRoute = getBusRoute(busService.serviceNo)
                if (busRoute != null) {
                    navigateToBusRouteInformation(
                        navController = navController,
                        serviceNo = busRoute.serviceNo,
                        direction = busRoute.direction,
                        stopSequence = busRoute.stopSequence,
                        showMap = true,
                        showLiveBuses = true
                    )
                } else {
                    setShowBusArrival(false)
                    Toast.makeText(
                        BusApplication.instance,
                        "Click on the bus service here",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ) {
                BusArrivalLayout(
                    data = busService.nextBus,
                    modifier = Modifier.weight(1f),
                    hasCoordinates = busStopInfo,
                    showBusType = showBusType
                )
                BusArrivalLayout(
                    data = busService.nextBus2,
                    modifier = Modifier.weight(1f),
                    hasCoordinates = busStopInfo,
                    showBusType = showBusType
                )
                BusArrivalLayout(
                    data = busService.nextBus3,
                    modifier = Modifier.weight(1f),
                    hasCoordinates = busStopInfo,
                    showBusType = showBusType
                )
            }
        }

    }
}

@Composable
fun BusArrivalLayout(
    data: Bus,
    hasCoordinates: HasCoordinates,
    modifier: Modifier = Modifier,
    showBusType: Boolean = false
) {


    Surface(
        color = getBusArrivalColor(data, isSystemInDarkTheme()),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (showBusType) {
                Image(
                    painter = painterResource(id = busTypeToPicture(data)),
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
                    text = if (data.isLive()) data.getDistanceFrom(hasCoordinates).toString() + "m" else "-",
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

