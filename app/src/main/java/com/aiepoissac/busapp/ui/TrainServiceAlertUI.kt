package com.aiepoissac.busapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.RailwayAlert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.mrtstation.AffectedSegment
import com.aiepoissac.busapp.data.mrtstation.FacilityMaintenance
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import com.aiepoissac.busapp.data.mrtstation.TrainServiceAlertsInfo

@Composable
fun TrainServiceAlertUI(
    navController: NavHostController
) {

    val trainServiceAlertViewModel: TrainServiceAlertViewModel = viewModel()
    val trainServiceAlertUIState by trainServiceAlertViewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = trainServiceAlertViewModel::refresh
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh information"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {

            CollapsibleSection(
                text = "Lift Maintenance",
                expanded = trainServiceAlertUIState.showFacilitiesMaintenance,
                onClick = trainServiceAlertViewModel::toggleShowFacilitiesMaintenance,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Elevator,
                        contentDescription = "Lift Maintenance",
                        modifier = Modifier.weight(1f)
                    )
                }
            ) {
                FacilitiesMaintenanceList(
                    navController = navController,
                    data = trainServiceAlertUIState.facilitiesMaintenance
                )
            }

            CollapsibleSection(
                text = "Train Service Alert",
                expanded = trainServiceAlertUIState.showTrainServiceAlerts,
                onClick = trainServiceAlertViewModel::toggleShowTrainServiceAlerts,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.RailwayAlert,
                        contentDescription = "Train Service Alerts",
                        modifier = Modifier.weight(1f)
                    )
                }
            ) {
                TrainServiceAlertList(
                    navController = navController,
                    trainServiceAlertsInfo = trainServiceAlertUIState.trainServiceAlerts?.value,
                    affectedSegmentsWithMRTStationsList = trainServiceAlertUIState.affectedSegmentsWithMRTStationList
                )
            }

        }
    }
}

@Composable
private fun TrainServiceAlertList(
    navController: NavHostController,
    trainServiceAlertsInfo: TrainServiceAlertsInfo?,
    affectedSegmentsWithMRTStationsList: List<Pair<AffectedSegment, List<MRTStation>>>?
) {

    if (trainServiceAlertsInfo == null || affectedSegmentsWithMRTStationsList == null) {
        Text(
            text = "Data is currently unavailable.",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        if (trainServiceAlertsInfo.status == 1) {
            Text(
                text = "Train services are operating normally.",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                modifier = Modifier.heightIn(max = 320.dp)
            ) {
                items(affectedSegmentsWithMRTStationsList) { affectedSegmentWithMRTStationsList ->
                    val affectedSegment = affectedSegmentWithMRTStationsList.first
                    val affectedMRTStations = affectedSegmentWithMRTStationsList.second
                    Card(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "${affectedSegment.line} ${affectedSegment.direction} direction",
                            textAlign = TextAlign.Left,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = "Affected stations (tap to view nearby bus stops): ",
                            textAlign = TextAlign.Left,
                            modifier = Modifier.padding(horizontal = 4.dp)
                                .fillMaxWidth()
                        )

                        affectedMRTStations.forEach {
                            Text(
                                text = it.toString(),
                                textAlign = TextAlign.Left,
                                modifier = Modifier.padding(horizontal = 4.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        navigateToNearby(
                                            navController = navController,
                                            latitude = it.latitude,
                                            longitude = it.longitude
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {

            items(trainServiceAlertsInfo.message) { message ->
                Text(
                    text = message.content,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 8.dp)
                        .fillMaxWidth()
                )
            }

        }


    }

}

@Composable
private fun FacilitiesMaintenanceList(
    navController: NavHostController,
    data: List<Pair<MRTStation, FacilityMaintenance>>?
) {

    if (data == null) {
        Text(
            text = "Lift maintenance data is currently unavailable.",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )
    } else {

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {

           items(data) { facilityMaintenance ->

               val mrtStation = facilityMaintenance.first

               Card(
                   modifier = Modifier.padding(8.dp),
                   onClick = {
                       navigateToNearby(
                           navController = navController,
                           latitude = mrtStation.latitude,
                           longitude = mrtStation.longitude
                       )
                   }
               ) {

                   Text(
                       text = "$mrtStation Lift: ${facilityMaintenance.second.liftID}",
                       textAlign = TextAlign.Left,
                       modifier = Modifier.padding(horizontal = 8.dp)
                           .fillMaxWidth()
                   )

                   Text(
                       text = facilityMaintenance.second.liftDescription,
                       textAlign = TextAlign.Left,
                       modifier = Modifier.padding(horizontal = 8.dp)
                           .fillMaxWidth()
                   )

               }

           }

        }

    }

}