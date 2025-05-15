package com.aiepoissac.busapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiepoissac.busapp.ui.theme.AiepoissacBusAppTheme

@Composable
fun BusArrivalUI(busArrivalViewModel: BusArrivalViewModel = viewModel()) {

    val busArrivalUIState by busArrivalViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier

    ) {

        Text(
            text = "Enter bus stop code",
            style = typography.titleLarge
        )
    }

}

@Preview
@Composable
fun BusAppPreview() {
    AiepoissacBusAppTheme {
        BusArrivalUI()
    }
}