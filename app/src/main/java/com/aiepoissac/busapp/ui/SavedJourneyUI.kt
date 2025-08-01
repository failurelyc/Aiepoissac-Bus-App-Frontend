package com.aiepoissac.busapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.userdata.JourneySegmentInfo

@Composable
fun SavedJourneyUI(
    navController: NavHostController,
    journeyID: String
) {

    val savedJourneyViewModel: SavedJourneyViewModel =
        viewModel(
            factory = SavedJourneyViewModelFactory(
                journeyID = journeyID
            )
        )

    val savedJourneyUIState by savedJourneyViewModel.uiState.collectAsState()

    if (savedJourneyUIState.showAddDialog) {
        Dialog(
            onDismissRequest = { savedJourneyViewModel.setShowAddDialog(false) }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .padding(4.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Add segment",
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = savedJourneyUIState.serviceNoInput,
                    singleLine = true,
                    onValueChange = savedJourneyViewModel::updateServiceNoInput,
                    label = { Text(text = "Service Number") },
                    isError = savedJourneyUIState.originStopSearchResults.isEmpty(),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                )

                Row {
                    Text(
                        text = "Direction 2?",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .weight(4f)
                            .align(Alignment.CenterVertically)
                    )

                    Switch(
                        checked = savedJourneyUIState.isDirectionTwo,
                        onCheckedChange = savedJourneyViewModel::setIsDirectionTwo,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .weight(1f)
                    )
                }

                val originBusStop = savedJourneyUIState.originStopInput
                val destinationBusStop = savedJourneyUIState.destinationStopInput

                OptionsSelector(
                    expanded = savedJourneyUIState.originStopSearchExpanded,
                    label = "Origin bus stop",
                    selected = originBusStop?.toString() ?: "",
                    selections = savedJourneyUIState.originStopSearchResults,
                    onExpandedChange = savedJourneyViewModel::setOriginStopSearchExpanded,
                    onItemClick = savedJourneyViewModel::updateOriginStopInput,
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                )

                OptionsSelector(
                    expanded = savedJourneyUIState.destinationStopSearchExpanded,
                    label = "Destination bus stop",
                    selected = destinationBusStop?.toString() ?: "",
                    selections = savedJourneyUIState.destinationStopSearchResults,
                    onExpandedChange = savedJourneyViewModel::setDestinationStopSearchExpanded,
                    onItemClick = savedJourneyViewModel::updateDestinationStopInput,
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = savedJourneyViewModel::addBusJourney
                ) {
                    TitleWithIcon(
                        title = "Add",
                        icon = Icons.Filled.Add
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = { savedJourneyViewModel.setShowAddDialog(false) }
                ) {
                    TitleWithIcon(
                        title = "Cancel and close",
                        icon = Icons.Filled.Cancel
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = savedJourneyViewModel::clearInput
                ) {
                    TitleWithIcon(
                        title = "Clear input",
                        icon = Icons.AutoMirrored.Filled.Backspace
                    )
                }
            }
        }
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = savedJourneyViewModel::refreshBusArrivals
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh bus arrivals")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { savedJourneyViewModel.setShowAddDialog(true) }
            ) {
                Text(
                    text = "Add journey segment",
                    textAlign = TextAlign.Center
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                IconTextSwitch(
                    icon = Icons.Filled.DirectionsBus,
                    text = "Bus type",
                    showText = false,
                    checked = savedJourneyUIState.showBusType,
                    onCheckedChange = savedJourneyViewModel::setShowBusType,
                    modifier = Modifier.weight(1f)
                )

                IconTextSwitch(
                    icon = Icons.Filled.AccessTime,
                    text = "First/Last Bus",
                    showText = false,
                    checked = savedJourneyUIState.showFirstLastBus,
                    onCheckedChange = savedJourneyViewModel::setShowFirstLastBus,
                    modifier = Modifier.weight(1f)
                )

                IconTextSwitch(
                    icon = Icons.Filled.DepartureBoard,
                    text = "Dest arrivals",
                    showText = true,
                    checked = savedJourneyUIState.showDestinationBusArrivals,
                    onCheckedChange = savedJourneyViewModel::setShowDestinationBusArrivals,
                    modifier = Modifier.weight(2f)
                )
            }

            SavedJourneySegmentList(
                navController = navController,
                uiState = savedJourneyUIState,
                onDelete = savedJourneyViewModel::deleteBusJourney,
                onMoveUp = savedJourneyViewModel::moveBusJourneyUp,
                onMoveDown = savedJourneyViewModel::moveBusJourneyDown
            )

        }
    }

}

@Composable
private fun SavedJourneySegmentList(
    navController: NavHostController,
    uiState: SavedJourneyUIState,
    onDelete: (JourneySegmentInfo) -> Unit,
    onMoveUp: (JourneySegmentInfo) -> Unit,
    onMoveDown: (JourneySegmentInfo) -> Unit
) {

    val data = uiState.segments

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp)
    ) {
        items(data) { busJourney ->
            SavedJourneySegmentInformation(
                navController = navController,
                data = busJourney,
                onDelete = onDelete,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown,
                showBusType = uiState.showBusType,
                showDestinationBusArrivals = uiState.showDestinationBusArrivals,
                showFirstLastBus = uiState.showFirstLastBus
            )
        }
    }

}

@Composable
private fun SavedJourneySegmentInformation(
    navController: NavHostController,
    data: Pair<JourneySegmentInfo, Pair<Pair<BusRouteInfoWithBusStopInfo, BusService?>, Pair<BusRouteInfoWithBusStopInfo, BusService?>>>,
    onDelete: (JourneySegmentInfo) -> Unit,
    onMoveUp: (JourneySegmentInfo) -> Unit,
    onMoveDown: (JourneySegmentInfo) -> Unit,
    showBusType: Boolean,
    showDestinationBusArrivals: Boolean,
    showFirstLastBus: Boolean
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier.weight(4f)
        ) {
            val originBusStop = data.second.first
            val destinationBusStop = data.second.second

            Text(
                text = "${originBusStop.first.busRouteInfo.operator} ${originBusStop.first.busRouteInfo.serviceNo}",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )

            SavedJourneySegmentBusStopInformation(
                navController = navController,
                busStop = originBusStop,
                showBusType = showBusType,
                showFirstLastBus = showFirstLastBus
            )

            if (showDestinationBusArrivals) {
                SavedJourneySegmentBusStopInformation(
                    navController = navController,
                    busStop = destinationBusStop,
                    showBusType = showBusType,
                    showFirstLastBus = showFirstLastBus
                )
            }
        }

        Card (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = data.first.sequence.toString(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )

            Icon(
                modifier = Modifier
                    .clickable { onDelete(data.first) }
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp),
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete"
            )

            Icon(
                modifier = Modifier
                    .clickable { onMoveUp(data.first) }
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp),
                imageVector = Icons.Filled.MoveUp,
                contentDescription = "Move up"
            )

            Icon(
                modifier = Modifier
                    .clickable { onMoveDown(data.first) }
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp),
                imageVector = Icons.Filled.MoveDown,
                contentDescription = "Move down"
            )
        }
    }
}

@Composable
private fun SavedJourneySegmentBusStopInformation(
    navController: NavHostController,
    busStop: Pair<BusRouteInfoWithBusStopInfo, BusService?>,
    showBusType: Boolean,
    showFirstLastBus: Boolean
) {

    Column(
        modifier = Modifier.clickable {
            navigateToBusRouteInformation(
                navController = navController,
                serviceNo = busStop.first.busRouteInfo.serviceNo,
                direction = busStop.first.busRouteInfo.direction,
                stopSequence = busStop.first.busRouteInfo.stopSequence
            )
        }
    ) {
        Text(
            text = busStop.first.toString(),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()

        )

        if (showFirstLastBus) {
            val busRouteInfo = busStop.first.busRouteInfo

            if (busRouteInfo.isOperatingOnWeekday()) {
                Text(
                    text = "WEEKDAY: ${busRouteInfo.wdFirstBus} to ${busRouteInfo.wdLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            if (busRouteInfo.isOperatingOnSaturday()) {
                Text(
                    text = "SATURDAY: ${busRouteInfo.satFirstBus} to ${busRouteInfo.satLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            if (busRouteInfo.isOperatingOnSunday()) {
                Text(
                    text = "SUNDAY/PH: ${busRouteInfo.sunFirstBus} to ${busRouteInfo.sunLastBus}",
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }

        val busService = busStop.second

        if (busService == null) {
            Text(
                text = "Bus arrival data not available",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                BusArrivalLayout(
                    data = busService.nextBus,
                    modifier = Modifier.weight(1f),
                    hasCoordinates = busStop.first.busStopInfo,
                    showBusType = showBusType
                )
                BusArrivalLayout(
                    data = busService.nextBus2,
                    modifier = Modifier.weight(1f),
                    hasCoordinates = busStop.first.busStopInfo,
                    showBusType = showBusType
                )
                BusArrivalLayout(
                    data = busService.nextBus3,
                    modifier = Modifier.weight(1f),
                    hasCoordinates = busStop.first.busStopInfo,
                    showBusType = showBusType
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> OptionsSelector(
    expanded: Boolean,
    selected: String,
    label: String,
    selections: List<T>,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (T) -> Unit,
    modifier: Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            readOnly = true,
            label = { Text(text = label) },
            onValueChange = {},
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            selections.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.toString()) },
                    onClick = {
                        onItemClick(item)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

