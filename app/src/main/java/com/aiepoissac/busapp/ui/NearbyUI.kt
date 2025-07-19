package com.aiepoissac.busapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Subway
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.GoogleMapsURLGenerator
import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.MRTStation
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyUI(
    navController: NavHostController,
    latitude: Double,
    longitude: Double,
    isLiveLocation: Boolean
) {

    val nearbyViewModel: NearbyViewModel =
        viewModel(
            factory = NearbyViewModelFactory(
                latitude = latitude,
                longitude = longitude,
                isLiveLocation = isLiveLocation
            )
        )

    val nearbyUIState by nearbyViewModel.uiState.collectAsState()
    val cameraPositionState by nearbyViewModel.cameraPositionState.collectAsState()
    val markerState by nearbyViewModel.markerState.collectAsState()

    val configuration = LocalConfiguration.current

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
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            if (nearbyUIState.mrtStationList.isNotEmpty()) {

                if (configuration.orientation == 1) {
                    CollapsibleSection(
                        text = "Nearby MRT Stations",
                        expanded = nearbyUIState.showNearbyMRTStations,
                        onClick = { nearbyViewModel.toggleShowNearbyMRTStations() },
                        contents = {
                            MRTStationList(
                                navController = navController,
                                openDirections = nearbyViewModel::openDirectionsToMRTStation,
                                stopLiveLocation = nearbyViewModel::stopLiveLocation,
                                uiState = nearbyUIState
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Subway,
                                contentDescription = "Nearby MRT Stations",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    )

                    CollapsibleSection(
                        text = "Nearby Bus Stops",
                        expanded = nearbyUIState.showNearbyBusStops,
                        onClick = { nearbyViewModel.toggleShowNearbyBusStops() },
                        contents = {
                            BusStopList(
                                navController = navController,
                                openDirections = {
                                    nearbyViewModel.openDirections(
                                        destination = it,
                                        travelMode = GoogleMapsURLGenerator.TravelMode.Walking
                                    )
                                },
                                updateCameraPosition = nearbyViewModel::setCameraPositionToLocation,
                                stopLiveLocation = nearbyViewModel::stopLiveLocation,
                                uiState = nearbyUIState,
                                showOnlyOperatingBusServicesOnCheckedChange = nearbyViewModel::setShowOnlyOperatingBusServices,
                                setDayOfWeek = nearbyViewModel::setDayOfWeek,
                                setShowTimeDial = nearbyViewModel::setShowTimeDial,
                                updateTime = nearbyViewModel::updateTime
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.DirectionsBus,
                                contentDescription = "Nearby Bus Stops",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    )
                }

                if (!nearbyUIState.showNearbyBusStops && !nearbyUIState.showNearbyMRTStations) {
                    Row {

                        IconTextSwitch(
                            icon = Icons.Filled.DirectionsBus,
                            text = "Bus stops",
                            showText = false,
                            checked = nearbyUIState.showNearbyBusStopsOnMap,
                            onCheckedChange = nearbyViewModel::setShowNearbyBusStopsOnMap,
                            modifier = Modifier.weight(3f)
                        )

                        IconTextSwitch(
                            icon = Icons.AutoMirrored.Filled.DirectionsBike,
                            text = "Bicycle Parking",
                            showText = false,
                            checked = nearbyUIState.showBicycleParkingOnMap,
                            onCheckedChange = nearbyViewModel::setShowBicycleParkingOnMap,
                            modifier = Modifier.weight(3f)
                        )

                        Button(
                            onClick = nearbyViewModel::recenterCamera,
                            modifier = Modifier.weight(2f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationSearching,
                                contentDescription = "Recenter camera"
                            )
                        }

                    }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxWidth(),
                    cameraPositionState = cameraPositionState,
                    contentDescription = "Map of nearby bus stops",
                    onMapClick = nearbyViewModel::updateLocation
                ) {

                    Marker(
                        state = markerState,
                        title = "Selected location",
                        snippet = "${markerState.position.latitude}, ${markerState.position.longitude}"
                    )

                    val showMoreDetails = cameraPositionState.position.zoom >= 17f &&
                            !(nearbyUIState.showNearbyBusStops && nearbyUIState.showNearbyMRTStations)

                    if (nearbyUIState.showNearbyBusStopsOnMap) {
                        nearbyUIState.busStopList.forEach {
                            val busStopInfo = it.second.busStopInfo
                            MarkerComposable(
                                keys = arrayOf(nearbyUIState.busStopList, showMoreDetails),
                                state = MarkerState(position = LatLng(busStopInfo.latitude, busStopInfo.longitude)),
                                onClick = {
                                    nearbyViewModel.stopLiveLocation()
                                    navigateToBusArrival(
                                        navController = navController,
                                        busStopInput = busStopInfo.busStopCode
                                    )
                                    return@MarkerComposable true
                                }
                            ) {
                                Card(
                                    modifier = Modifier.widthIn(max = 240.dp)
                                ) {
                                    if (showMoreDetails) {
                                        Text(
                                            text = "${busStopInfo.busStopCode} ${busStopInfo.description} (${it.first}m)",
                                            fontSize = 10.sp,
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(horizontal = 2.dp)
                                        )

                                        Text(
                                            text = it.second.busRoutesInfo
                                                .map{ it.serviceNo }
                                                .joinToString(", ")
                                            ,
                                            fontSize = 8.sp,
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(horizontal = 2.dp)
                                        )


                                    } else {
                                        Text(
                                            text = it.first.toString() + "m",
                                            fontSize = 8.sp,
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(horizontal = 2.dp)
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Filled.DirectionsBus,
                                        contentDescription = "Bus Stop",
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }

                    if (nearbyUIState.showBicycleParkingOnMap) {
                        val bicycleParkingList = nearbyUIState.bicycleParkingList
                        if (bicycleParkingList != null) {
                            bicycleParkingList.forEach {
                                val bicycleParking = it.second
                                MarkerComposable(
                                    keys = arrayOf(bicycleParkingList, showMoreDetails),
                                    state = MarkerState(position = LatLng(bicycleParking.latitude, bicycleParking.longitude)),
                                    onClick = {
                                        nearbyViewModel.openDirections(
                                            destination = bicycleParking,
                                            travelMode = GoogleMapsURLGenerator.TravelMode.Bicycling
                                        )
                                        return@MarkerComposable true
                                    }
                                ) {
                                    Card(
                                        modifier = Modifier.widthIn(max = 240.dp)
                                    ) {
                                        if (showMoreDetails) {
                                            Text(
                                                text = "${bicycleParking.rackCount} (${it.first}m)",
                                                fontSize = 10.sp,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .padding(horizontal = 2.dp)
                                            )

                                            Text(
                                                text = "${bicycleParking.rackType} " +
                                                        if (bicycleParking.hasShelter()) "sheltered" else "",
                                                fontSize = 8.sp,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .padding(horizontal = 2.dp)
                                            )


                                        } else {
                                            Text(
                                                text = it.first.toString() + "m",
                                                fontSize = 8.sp,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .padding(horizontal = 2.dp)
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                                            contentDescription = "Bicycle parking",
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                BusApplication.instance,
                                "Failed to obtain bicycle parking locations",
                                Toast.LENGTH_SHORT
                            ).show()
                            nearbyViewModel.setShowBicycleParkingOnMap(false)
                        }
                    }
                }

            } else {
                LoadingScreen(description = "Loading nearby bus and MRT stations")
            }
        }
    }
}

@Composable
fun LoadingScreen(description: String) {
    Column {
        Text(
            text = description,
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

@Composable
fun CollapsibleSection(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (expanded) "Collapse $text" else "Expand $text"
        )
    },
    contents: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                    role = Role.Button
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }

            Text(
                text = text,
                fontSize = 20.sp,
                modifier = Modifier.weight(5f)
            )

            if (trailingIcon != null) {
                trailingIcon()
            }

        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            contents()
        }
    }
}

@Composable
private fun MRTStationList(
    navController: NavHostController,
    openDirections: (MRTStation) -> Unit,
    stopLiveLocation: () -> Unit,
    uiState: NearbyUIState
) {

    val data = uiState.mrtStationList

    if (data.isNotEmpty()) {
        Text(
            text = "Click on an MRT station to view alternative bus services to this MRT station.",
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            items(data) { mrtStation ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        stopLiveLocation()
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

                    Icon(
                        imageVector = Icons.Filled.Directions,
                        contentDescription = "directions",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                openDirections(mrtStation.second)
                            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusStopList(
    navController: NavHostController,
    openDirections: (HasCoordinates) -> Unit,
    updateCameraPosition: (HasCoordinates) -> Unit,
    stopLiveLocation: () -> Unit,
    showOnlyOperatingBusServicesOnCheckedChange: (Boolean) -> Unit,
    setDayOfWeek: () -> Unit,
    setShowTimeDial: (Boolean) -> Unit,
    updateTime: (TimePickerState) -> Unit,
    uiState: NearbyUIState
) {

    val data = uiState.busStopList

    TimeSettings(
        showOnlyOperatingBusServices = uiState.showOnlyOperatingBusServices,
        showOnlyOperatingBusServicesOnCheckedChange = showOnlyOperatingBusServicesOnCheckedChange,
        showTimeDial = uiState.showTimeDial,
        updateTime = updateTime,
        setShowTimeDial = setShowTimeDial,
        currentDayOfWeek = uiState.dayOfWeek,
        setDayOfWeek = setDayOfWeek,
        currentTime = uiState.currentTime
    )

    if (uiState.searchingForBusStops) {
        LoadingScreen(description = "Searching for bus stops")
    } else if (data.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            items(data) { busStop ->
                val busStopInfo = busStop.second.busStopInfo
                Card(
                    onClick = {
                        stopLiveLocation()
                        navigateToBusArrival(
                            navController = navController,
                            busStopInput = busStopInfo.busStopCode
                        )
                              },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row {

                        Column(
                            modifier = Modifier.weight(5f)
                        ) {
                            Text(
                                text = "${busStopInfo.busStopCode} ${busStopInfo.description}",
                                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                            )
                            Text(
                                text = "${busStopInfo.roadName} (${busStop.first}m)",
                                modifier = Modifier.padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "location of bus stop",
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    updateCameraPosition(busStop.second.busStopInfo)
                                }
                        )

                        Icon(
                            imageVector = Icons.Filled.Directions,
                            contentDescription = "directions to bus stop",
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    openDirections(busStopInfo)
                                }
                        )
                    }
                    Text(
                        text = busStop.second.busRoutesInfo
                            .map{ it.serviceNo }
                            .joinToString(separator = ", ")
                        ,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

            }
        }
    } else {
        Text(
            text = "No nearby bus stops. Try another location.",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSettings(
    showOnlyOperatingBusServices: Boolean,
    showOnlyOperatingBusServicesOnCheckedChange: (Boolean) -> Unit,
    showTimeDial: Boolean,
    updateTime: (TimePickerState) -> Unit,
    setShowTimeDial: (Boolean) -> Unit,
    currentDayOfWeek: DayOfWeek,
    setDayOfWeek: () -> Unit,
    currentTime: LocalTime
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Only show bus services operating",
            modifier = Modifier.padding(horizontal = 4.dp)
                .align(Alignment.CenterVertically)
        )
        Checkbox(
            checked = showOnlyOperatingBusServices,
            onCheckedChange = showOnlyOperatingBusServicesOnCheckedChange
        )
    }


    if (showOnlyOperatingBusServices) {

        if (showTimeDial) {
            TimeSettingDialog(
                onConfirm = updateTime,
                onDismiss = { setShowTimeDial(false) }
            )
        }

        val day =
            when (currentDayOfWeek) {
                DayOfWeek.SATURDAY -> "Saturdays"
                DayOfWeek.SUNDAY -> "Sundays and Public Holidays"
                else -> "Weekdays"
            }

        Text(
            text = "$day (5am to 3am)",
            modifier = Modifier.padding(horizontal = 4.dp)
                .fillMaxWidth()
                .clickable {
                    setDayOfWeek()
                }
        )

        Text(
            text = "Time: $currentTime (click to change)",
            modifier = Modifier.padding(horizontal = 4.dp)
                .fillMaxWidth()
                .clickable {
                    setShowTimeDial(true)
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSettingDialog(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        val currentTime = Calendar.getInstance()

        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = true,
        )

        Column {
            TimePicker(
                state = timePickerState,
            )
            Button(onClick = onDismiss) {
                Text("Dismiss picker")
            }
            Button(onClick = { onConfirm(timePickerState) }) {
                Text("Confirm selection")
            }
        }
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
