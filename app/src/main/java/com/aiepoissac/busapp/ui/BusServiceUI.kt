package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo

@Composable
fun BusServiceUI(
    navController: NavHostController,
    busServiceInput: String
) {

    val busServiceViewModel: BusServiceViewModel =
        viewModel(factory = BusServiceViewModelFactory(
            busServiceInput = busServiceInput))

    val busServiceUIState by busServiceViewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BusServiceNoForBusServiceInformation(
                onBusServiceNoChanged = { busServiceViewModel.updateBusServiceNoInput(it) },
                onKeyBoardDone = { busServiceViewModel.updateBusService() },
                busServiceNoInput = busServiceViewModel.busServiceNoInput,
                isError = busServiceUIState.busServiceList.isEmpty()
            )
            BusServicesList(navController = navController, uiState = busServiceUIState)
        }


    }

}

@Composable
private fun BusServiceNoForBusServiceInformation(
    onBusServiceNoChanged: (String) -> Unit,
    onKeyBoardDone: () -> Unit,
    busServiceNoInput: String,
    isError: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = busServiceNoInput,
            singleLine = true,
            onValueChange = onBusServiceNoChanged,
            label = { Text("Bus Service No") },
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {onKeyBoardDone()}),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )
    }

}

@Composable
private fun BusServicesList(
    navController: NavHostController,
    uiState: BusServiceUIState
) {

    val data = uiState.busServiceList

    if (data.isNotEmpty()) {
        Text(
            text = "Click on a bus service to view its full route" ,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )

        LazyVerticalGrid(
            modifier = Modifier,
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {
            items(data) { service ->
                BusServiceInformation(navController = navController, busService = service)
            }
        }
    } else {
        Text(
            text = "No such bus service" ,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@Composable
private fun BusServiceInformation(
    navController: NavHostController,
    busService: BusServiceInfoWithBusStopInfo
) {
    val busServiceInfo = busService.busServiceInfo

    Card(
        modifier = Modifier.padding(16.dp),
        onClick = { navigateToBusRouteInformation(
            navController = navController,
            serviceNo = busServiceInfo.serviceNo,
            direction = busServiceInfo.direction) }
    ) {

        Text(
            text = "${busServiceInfo.operator} ${busServiceInfo.category} ${busServiceInfo.serviceNo}" ,
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "${busService.originBusStopInfo.description} to ${busService.destinationBusStopInfo.description}" ,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (busServiceInfo.isLoop()) {
            Text(
                text = "Loop at: ${busServiceInfo.loopDesc}" ,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (busServiceInfo.hasAMPeakFrequency()) {
            Text(
                text = "0630H - 0830H: ${busServiceInfo.amPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (busServiceInfo.hasAMOffPeakFrequency()) {
            Text(
                text = "0831H - 1659H: ${busServiceInfo.amOffPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (busServiceInfo.hasPMPeakFrequency()) {
            Text(
                text = "1700H - 1900H: ${busServiceInfo.pmPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (busServiceInfo.hasPMOffPeakFrequency()) {
            Text(
                text = "After 1900H: ${busServiceInfo.pmOffPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }


    }
}