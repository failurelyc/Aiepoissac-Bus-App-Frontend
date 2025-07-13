package com.aiepoissac.busapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.isLoop
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun BusRouteUI(
    navController: NavHostController,
    serviceNo: String,
    direction: Int,
    stopSequence: Int,
    showMap: Boolean,
    showLiveBuses: Boolean
) {

    val busRouteViewModel: BusRouteViewModel =
        viewModel(
            factory = BusRouteViewModelFactory(
                serviceNo = serviceNo,
                direction = direction,
                stopSequence = stopSequence,
                showMap = showMap,
                showLiveBuses = showLiveBuses
            )
        )

    val busRouteUIState by busRouteViewModel.uiState.collectAsState()
    val cameraPositionState by busRouteViewModel.cameraPositionState.collectAsState()
    val markerState by busRouteViewModel.markerState.collectAsState()

    val gridState = rememberLazyGridState()

    RequestLocationPermission {
        busRouteViewModel.updateLiveLocation()
    }

    Scaffold (
        floatingActionButton = {
            if (!busRouteUIState.showMap) {
                FloatingActionButton(
                    onClick = {
                        MainScope().launch {
                            gridState.scrollToItem(0)
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.VerticalAlignTop,
                        contentDescription = "Scroll to top"
                    )
                }
            } else if (busRouteUIState.showLiveBuses) {
                FloatingActionButton(
                    onClick = {
                        busRouteViewModel.refreshLiveBuses()
                    }
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Refresh live bus locations"
                    )
                }
            }
        },
        bottomBar = {
            if (busRouteUIState.busServiceVariants.size > 1) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        busRouteUIState.busServiceVariants.forEach {

                            Card(
                                onClick = { busRouteViewModel.updateBusService(it.busServiceInfo) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = it.busServiceInfo.serviceNo,
                                    fontSize = 24.sp,
                                    color = if (it.busServiceInfo != busRouteUIState.busServiceInfo) Color.Unspecified else Color.Blue,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = it.destinationBusStopInfo.description,
                                    fontSize = 8.sp,
                                    lineHeight = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                        }
                    }
                }
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
                    busRouteViewModel.setOriginalFirstBusStop(truncateAfterLoopingPoint = false)
                    MainScope().launch {
                        gridState.scrollToItem(0)
                    }
                                      },
                setIsLiveLocationOnClick = busRouteViewModel::setIsLiveLocation,
                setShowFirstLastBusOnClick = busRouteViewModel::setShowFirstLastBusTimings,
                setShowLiveBusesOnClick = busRouteViewModel::setShowLiveBuses,
                setShowMapOnClick = busRouteViewModel::setShowMap,
                showRouteFromLoopingPointOnClick = {
                    busRouteViewModel.setLoopingPointAsFirstBusStop()
                    MainScope().launch {
                        gridState.scrollToItem(0)
                    }
                                                   },
                setFirstBusStop = busRouteViewModel::setFirstBusStop,
                gridState = gridState,
                cameraPositionState = cameraPositionState,
                markerState = markerState
            )
        }
    }
}

@Composable
private fun BusRouteList(
    navController: NavHostController,
    uiState: BusRouteUIState,
    revertButtonOnClick: () -> Unit,
    setIsLiveLocationOnClick: (Boolean) -> Unit,
    setShowFirstLastBusOnClick: (Boolean) -> Unit,
    setShowLiveBusesOnClick: (Boolean) -> Unit,
    setShowMapOnClick: (Boolean) -> Unit,
    showRouteFromLoopingPointOnClick: () -> Unit,
    setFirstBusStop: (Int) -> Unit,
    gridState: LazyGridState,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState
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

                IconTextSwitch(
                    icon = if (uiState.isLiveLocation) Icons.Filled.LocationOn else Icons.Filled.LocationOff,
                    text = "location",
                    showText = false,
                    modifier = Modifier.weight(1f),
                    checked = uiState.isLiveLocation,
                    onCheckedChange = setIsLiveLocationOnClick
                )

                if (!uiState.showMap) {
                    IconTextSwitch(
                        icon = Icons.Filled.AccessTime,
                        text = "First/Last bus timing",
                        showText = false,
                        modifier = Modifier.weight(1f),
                        checked = uiState.showFirstLastBus,
                        onCheckedChange = setShowFirstLastBusOnClick
                    )
                } else {
                    IconTextSwitch(
                        icon = Icons.Filled.DepartureBoard,
                        text = "Live buses",
                        showText = false,
                        modifier = Modifier.weight(1f),
                        checked = uiState.showLiveBuses,
                        onCheckedChange = setShowLiveBusesOnClick
                    )
                }

                IconTextSwitch(
                    icon = Icons.Filled.Map,
                    text = "Map",
                    showText = false,
                    modifier = Modifier.weight(1f),
                    checked = uiState.showMap,
                    onCheckedChange = setShowMapOnClick
                )

                Text(
                    text = "${if (uiState.isLiveLocation) uiState.currentSpeed else "-"}km/h",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

            }

            if (uiState.truncated) {
                Text(
                    text = "Route from ${data.first().second.busStopInfo.busStopCode} ${data.first().second.busStopInfo.description} " +
                            "to ${data.last().second.busStopInfo.busStopCode} ${data.last().second.busStopInfo.description} is shown",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else if (uiState.truncatedAfterLoopingPoint) {
                Text(
                    text = "Route from looping point is shown.",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Text(
                    text = "Full route is shown.",
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
        }

        if (!uiState.showMap) {
            LazyVerticalGrid(
                modifier = Modifier,
                state = gridState,
                columns = GridCells.Adaptive(minSize = 320.dp)
            ) {
                items(data) { busRoute ->
                    BusRouteInformation(
                        navController = navController,
                        data = busRoute,
                        setIsLiveLocationOnClick = setIsLiveLocationOnClick,
                        uiState = uiState,
                        gridState = gridState,
                        setFirstBusStop = setFirstBusStop
                    )
                }
            }
        } else {

            if (uiState.showLiveBuses) {
                Text(
                    text = "Maximum of three buses shown within 3km intervals.",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxWidth(),
                cameraPositionState = cameraPositionState,
                contentDescription = "Map of bus route"
            ) {

                if (uiState.isLiveLocation) {
                    Marker(
                        state = markerState
                    )
                }

                val marked = HashSet<LatLng>()
                val zoomedIn = cameraPositionState.position.zoom >= 15f

                if (uiState.showLiveBuses) {
                    uiState.liveBuses.forEach {
                        MarkerComposable(
                            keys = arrayOf(uiState.liveBuses, zoomedIn),
                            state = MarkerState(position = LatLng(it.latitude, it.longitude))
                        ) {
                            Surface(
                                color = getBusArrivalColor(it, isSystemInDarkTheme())
                            ) {

                                Image(
                                    painter = painterResource(id = busTypeToPicture(it)),
                                    contentDescription = "Live bus",
                                    modifier = if (zoomedIn) Modifier.heightIn(max = 50.dp).widthIn(max = 50.dp)
                                        else Modifier.heightIn(max = 25.dp).widthIn(max = 25.dp)
                                )

                            }
                        }
                    }
                }

                uiState.busRoute.forEach {
                    val busStopInfo = it.second.busStopInfo
                    val busRouteInfo = it.second.busRouteInfo

                    val latLng = LatLng(busStopInfo.latitude, busStopInfo.longitude)
                    marked.add(latLng)

                    MarkerComposable(
                        keys = arrayOf(uiState.busRoute, zoomedIn),
                        state = MarkerState(position = latLng),
                        onClick = {
                            setIsLiveLocationOnClick(false)
                            navigateToBusArrival(
                                navController = navController,
                                busStopInput = busRouteInfo.busStopCode
                            )
                            return@MarkerComposable true
                        }
                    ) {
                        if (zoomedIn) {
                            Card {
                                Text(
                                    text = "${busRouteInfo.stopSequence} (${String.format("%.1f km", busRouteInfo.distance)})",
                                    fontSize = 8.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Icon(
                                    imageVector = Icons.Filled.DirectionsBus,
                                    contentDescription = "Bus stop",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Text(
                                    text = busRouteInfo.busStopCode,
                                    fontSize = 10.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.DirectionsBus,
                                contentDescription = "Bus stop",
                                tint = Color.Black
                            )
                        }
                    }

                }

                uiState.originalBusRoute
                    .filter {
                        !marked.contains(LatLng(it.busStopInfo.latitude, it.busStopInfo.longitude))
                    }
                    .forEach {
                        val busStopInfo = it.busStopInfo
                        val busRouteInfo = it.busRouteInfo

                        MarkerComposable(
                            state = MarkerState(position = LatLng(busStopInfo.latitude, busStopInfo.longitude)),
                            onClick = {
                                setIsLiveLocationOnClick(false)
                                navigateToBusArrival(
                                    navController = navController,
                                    busStopInput = busRouteInfo.busStopCode
                                )
                                return@MarkerComposable true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DirectionsBus,
                                contentDescription = "Bus Stop outside selected route",
                                tint = Color.LightGray
                            )
                        }

                    }



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
    setIsLiveLocationOnClick: (Boolean) -> Unit,
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
            onClick = {
                setIsLiveLocationOnClick(false)
                navigateToBusArrival(
                    navController = navController,
                    busStopInput = data.second.busRouteInfo.busStopCode
                )
            }
        ) {

            val busRouteInfo = data.second.busRouteInfo
            val busStopInfo = data.second.busStopInfo

            Text(
                text = busStopInfo.toString(),
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )

                Text(
                    text = "SATURDAY: ${busRouteInfo.satFirstBus} to ${busRouteInfo.satLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )

                Text(
                    text = "SUNDAY: ${busRouteInfo.sunFirstBus} to ${busRouteInfo.sunLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
            }
        }
    }
}