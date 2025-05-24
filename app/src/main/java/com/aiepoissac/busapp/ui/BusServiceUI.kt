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
import com.aiepoissac.busapp.data.businfo.BusServiceInfo

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

    val data: List<BusServiceInfo> = uiState.busServiceList

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
                BusServiceInformation(navController = navController, data = service)
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
    data: BusServiceInfo
) {
    Card(
        modifier = Modifier.padding(16.dp),
        onClick = { navigateToBusRouteInformation(
            navController = navController,
            serviceNo = data.serviceNo,
            direction = data.direction) }
    ) {

        Text(
            text = "${data.operator} ${data.category} ${data.serviceNo}" ,
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (!data.isLoop()) {
            Text(
                text = "Direction ${data.direction}" ,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = "${data.originCode} to ${data.destinationCode}" ,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (data.isLoop()) {
            Text(
                text = "Loop at: ${data.loopDesc}" ,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (data.hasAMPeakFrequency()) {
            Text(
                text = "0630H - 0830H: ${data.amPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (data.hasAMOffPeakFrequency()) {
            Text(
                text = "0831H - 1659H: ${data.amOffPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (data.hasPMPeakFrequency()) {
            Text(
                text = "1700H - 1900H: ${data.pmPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (data.hasPMOffPeakFrequency()) {
            Text(
                text = "After 1900H: ${data.pmOffPeakFreq} minutes",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }


    }
}

fun navigateToBusRouteInformation(
    navController: NavHostController,
    serviceNo: String = "",
    direction: Int
) {
    navController.navigate(Pages.BusRouteInformation.with2Text(serviceNo, direction.toString()))
}