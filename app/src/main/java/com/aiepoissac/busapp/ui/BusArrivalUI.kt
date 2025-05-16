package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.busarrival.BusStop
import com.aiepoissac.busapp.ui.theme.AiepoissacBusAppTheme

@Composable
fun BusArrivalUI(busArrivalViewModel: BusArrivalViewModel = viewModel()) {

    val busArrivalUIState by busArrivalViewModel.uiState.collectAsState()

    Column (
        modifier = Modifier.fillMaxWidth()

    ) {
        BusStopCodeForBusArrival(
            onBusStopCodeChanged = { busArrivalViewModel.updateBusStopCodeInput(it) },
            onKeyBoardDone = { busArrivalViewModel.updateBusStop() },
            busStopCodeInput = busArrivalViewModel.busStopCodeInput
        )
        BusArrivalsList(
            uiState = busArrivalUIState,
            onRefresh = { busArrivalViewModel.refreshBusArrival() })
    }

}

@Composable
fun BusStopCodeForBusArrival(
    onBusStopCodeChanged: (String) -> Unit,
    onKeyBoardDone: () -> Unit,
    busStopCodeInput: String) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            fontSize = 36.sp,
            text = "Bus Arrival Time",
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = busStopCodeInput,
            singleLine = true,
            onValueChange = onBusStopCodeChanged,
            label = {Text("Bus Stop Code")},
            isError = false,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {onKeyBoardDone()}),
            modifier = Modifier.padding(16.dp)
        )



    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusArrivalsList(
    uiState: BusArrivalUIState,
    onRefresh: () -> Unit) {

    val data: BusStop? = uiState.busArrivalData

    if (uiState.networkIssue) {
        Text(
            text = "Error in getting data. Check your internet connection!",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    } else if (data != null && data.services.isNotEmpty()) {
        Text(
            fontSize = 24.sp,
            text = "Bus Stop ${data.busStopCode}",
            modifier = Modifier.padding(8.dp)
        )
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
        ) {
            LazyColumn(modifier = Modifier) {
                items(data.services) { service ->
                    BusArrivalsLayout(data = service)
                }
            }
        }
    } else {
        Text(
            text = "Bus stop not found or no buses in operation",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
    }


}

@Composable
fun BusArrivalsLayout(data: BusService) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Card(modifier = Modifier.weight(1f)) {

            Text(
                text = data.serviceNo,
                fontSize = 36.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
            Text(
                text = data.operator,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
        }
        Card(modifier = Modifier.weight(2f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                if (data.nextBus.isValid()) {
                    BusArrivalLayout(data = data.nextBus, modifier = Modifier.weight(1f))
                } else {
                    BusArrivalEmptyLayout(modifier = Modifier.weight(1f))
                }
                if (data.nextBus2.isValid()) {
                    BusArrivalLayout(data = data.nextBus2, modifier = Modifier.weight(1f))
                } else {
                    BusArrivalEmptyLayout(modifier = Modifier.weight(1f))
                }
                if (data.nextBus3.isValid()) {
                    BusArrivalLayout(data = data.nextBus3, modifier = Modifier.weight(1f))
                } else {
                    BusArrivalEmptyLayout(modifier = Modifier.weight(1f))
                }
            }
        }

    }
}

@Composable
fun BusArrivalLayout(data: Bus, modifier: Modifier = Modifier) {

    Surface(
        color =
            if (data.monitored == 0) {
                Color.LightGray
            } else if (data.load == "SEA") {
                Color.Green
            } else if (data.load == "SDA") {
                Color.Yellow
            } else if (data.load == "LSD") {
                Color.Red
            } else {
                Color.Black
            },
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.type,
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = "${data.getDuration()} min",
                color = Color.Black,
                modifier = Modifier.padding(4.dp)
            )
            if (data.visitNumber != "1") {
                Text(
                    text = "Visit: ${data.visitNumber}",
                    color = Color.Black,
                    modifier = Modifier.padding(4.dp)
                )
            }

        }
    }
}

@Composable
fun BusArrivalEmptyLayout(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NA",
                fontSize = 24.sp,
                modifier = Modifier.padding(4.dp)
            )

        }
    }
}

@Preview
@Composable
fun BusAppPreview() {
    AiepoissacBusAppTheme {
        BusArrivalUI()
    }
}