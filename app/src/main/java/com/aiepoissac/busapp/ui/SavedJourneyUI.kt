package com.aiepoissac.busapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.aiepoissac.busapp.userdata.BusJourneyInfo

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
            onDismissRequest = savedJourneyViewModel::toggleAddDialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Add segment",
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = savedJourneyUIState.serviceNoInput,
                    singleLine = true,
                    onValueChange = savedJourneyViewModel::updateServiceNoInput,
                    label = { Text(text = "Service Number") },
                    isError = savedJourneyUIState.originStopSearchResults.isEmpty(),
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
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
                        checked = !savedJourneyUIState.directionOne,
                        onCheckedChange = { savedJourneyViewModel.toggleDirection() },
                        modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
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
                    onItemClick = savedJourneyViewModel::updateOriginStopInput
                )

                OptionsSelector(
                    expanded = savedJourneyUIState.destinationStopSearchExpanded,
                    label = "Destination bus stop",
                    selected = destinationBusStop?.toString() ?: "",
                    selections = savedJourneyUIState.destinationStopSearchResults,
                    onExpandedChange = savedJourneyViewModel::setDestinationStopSearchExpanded,
                    onItemClick = savedJourneyViewModel::updateDestinationStopInput
                )

                Button(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    onClick = savedJourneyViewModel::addBusJourney
                ) {
                    Text(
                        text = "Add",
                        textAlign = TextAlign.Center
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
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                onClick = savedJourneyViewModel::toggleAddDialog
            ) {
                Text(
                    text = "Add journey segment",
                    textAlign = TextAlign.Center
                )
            }

            SavedJourneySegmentList(
                navController = navController,
                uiState = savedJourneyUIState,
                onDelete = savedJourneyViewModel::deleteBusJourney
            )

        }
    }

}

@Composable
private fun SavedJourneySegmentList(
    navController: NavHostController,
    uiState: SavedJourneyUIState,
    onDelete: (BusJourneyInfo) -> Unit
) {

    val data = uiState.busJourneys

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp)
    ) {
        items(data) { busJourney ->
            SavedJourneySegmentInformation(
                navController = navController,
                data = busJourney,
                onDelete = onDelete
            )
        }
    }

}

@Composable
private fun SavedJourneySegmentInformation(
    navController: NavHostController,
    data: Pair<BusJourneyInfo, Pair<Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>, Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>>>,
    onDelete: (BusJourneyInfo) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier.weight(5f)
        ) {
            val originBusStop = data.second.first
            val destinationBusStop = data.second.second

            Text(
                text = "${originBusStop.first.busRouteInfo.operator} ${originBusStop.first.busRouteInfo.serviceNo}",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                textAlign = TextAlign.Center
            )

            SavedJourneySegmentBusStopInformation(
                navController = navController,
                busStop = originBusStop
            )

            SavedJourneySegmentBusStopInformation(
                navController = navController,
                busStop = destinationBusStop
            )
        }
        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = data.first.sequence.toString(),
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                textAlign = TextAlign.Center
            )

            Icon(
                modifier = Modifier
                    .clickable { onDelete(data.first) }
                    .align(Alignment.CenterHorizontally),
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

@Composable
private fun SavedJourneySegmentBusStopInformation(
    navController: NavHostController,
    busStop: Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>
) {

    Text(
        text = busStop.first.toString(),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { navigateToBusRouteInformation(
                navController = navController,
                serviceNo = busStop.first.busRouteInfo.serviceNo,
                direction = busStop.first.busRouteInfo.direction,
                stopSequence = busStop.first.busRouteInfo.stopSequence
            ) }
    )

    val busServices = busStop.second

    if (busServices == null) {
        Text(
            text = "Bus arrival data not available",
            modifier = Modifier.fillMaxWidth()
        )
    } else if (busServices.isEmpty()) {
        Text(
            text = "Bus not in operation",
            modifier = Modifier.fillMaxWidth()
        )
    } else if (busServices.size == 1) {
        val busService = busServices[0]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            BusArrivalLayout(
                data = busService.nextBus,
                modifier = Modifier.weight(1f),
                hasCoordinates = busStop.first.busStopInfo,
                hideBusType = true
            )
            BusArrivalLayout(
                data = busService.nextBus2,
                modifier = Modifier.weight(1f),
                hasCoordinates = busStop.first.busStopInfo,
                hideBusType = true
            )
            BusArrivalLayout(
                data = busService.nextBus3,
                modifier = Modifier.weight(1f),
                hasCoordinates = busStop.first.busStopInfo,
                hideBusType = true
            )
        }
    } else {
        Button(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            onClick = {
                navigateToBusArrival(
                    navController = navController,
                    busStopInput = busStop.first.busStopInfo.busStopCode
                )
            }
        ) {
            Text(
                text = "View bus arrivals here",
                textAlign = TextAlign.Center
            )
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
    onItemClick: (T) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.padding(8.dp).fillMaxWidth()
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

