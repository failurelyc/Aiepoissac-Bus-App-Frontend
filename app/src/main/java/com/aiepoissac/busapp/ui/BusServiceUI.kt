package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.navigation.NavController
import com.aiepoissac.busapp.data.businfo.BusServiceInfo

@Composable
fun BusServiceUI(
    navController: NavController,
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
            BusServicesList(uiState = busServiceUIState)
        }


    }

}

@Composable
fun BusServiceNoForBusServiceInformation(
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
fun BusServicesList(uiState: BusServiceUIState) {

    val data: List<BusServiceInfo> = uiState.busServiceList

    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.Adaptive(minSize = 320.dp)
    ) {
        items(data) { service ->
            BusServiceInformation(data = service)
        }
    }

}

@Composable
fun BusServiceInformation(data: BusServiceInfo) {
    Card(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "${data.category} ${data.serviceNo}" ,
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



    }
}