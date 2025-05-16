package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.busarrival.BusStop
import com.aiepoissac.busapp.data.busarrival.getBusArrival
import com.aiepoissac.busapp.ui.theme.AiepoissacBusAppTheme

@Composable
fun BusArrivalUI(busArrivalViewModel: BusArrivalViewModel = viewModel()) {

    //val busArrivalUIState by busArrivalViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier

    ) {



        BusArrivalsList(
            data = getBusArrival(65191)
        )
    }

}

@Composable
fun BusArrivalsList(data: BusStop) {
    LazyColumn(modifier = Modifier) {
        items(data.services) {
            service -> BusArrivalsLayout(data = service)
        }
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
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                if (data.nextBus.isValid()) {
                    BusArrivalLayout(data = data.nextBus)
                }
                if (data.nextBus2.isValid()) {
                    BusArrivalLayout(data = data.nextBus2)
                } else {
                    BusArrivalEmptyLayout()
                }
                if (data.nextBus3.isValid()) {
                    BusArrivalLayout(data = data.nextBus3)
                } else {
                    BusArrivalEmptyLayout()
                }
            }
        }

    }
}

@Composable
fun BusArrivalLayout(data: Bus) {

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
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.type,
                fontSize = 24.sp,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = "${data.getDuration()} min",
                modifier = Modifier.padding(4.dp)
            )
            if (data.visitNumber != "1") {
                Text(
                    text = "Visit: ${data.visitNumber}",
                    modifier = Modifier.padding(4.dp)
                )
            }

        }
    }
}

@Composable
fun BusArrivalEmptyLayout() {
    Surface {
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