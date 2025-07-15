package com.aiepoissac.busapp.ui

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.userdata.BusJourneyListInfo

@Composable
fun SavedJourneysUI(
    navController: NavHostController
) {

    val savedJourneysViewModel: SavedJourneysViewModel =
        viewModel(factory = SavedJourneysViewModelFactory())

    val savedJourneysUIState by savedJourneysViewModel.uiState.collectAsState()

    if (savedJourneysUIState.showAddDialog) {
        val selectedSavedJourney = savedJourneysUIState.selectedSavedJourney

        TextFieldDialog(
            value = savedJourneysUIState.descriptionInput,
            title = if (selectedSavedJourney != null) "Editing ${selectedSavedJourney.description}"
                    else "Add journey",
            label = "New description",
            onValueChange = savedJourneysViewModel::updateDescriptionInput,
            onDismissRequest = savedJourneysViewModel::toggleShowAddDialog,
            onConfirmRequest = savedJourneysViewModel::updateSavedJourney
        )
    }

    if (savedJourneysUIState.showDeleteDialog) {
        val selectedSavedJourney = savedJourneysUIState.selectedSavedJourney

        if (selectedSavedJourney != null) {
            TextFieldDialog(
                value = savedJourneysUIState.descriptionInput,
                title = "Confirm deletion of ${selectedSavedJourney.description} by typing the description again",
                label = "Description",
                isError = selectedSavedJourney.description != savedJourneysUIState.descriptionInput,
                onValueChange = savedJourneysViewModel::updateDescriptionInput,
                onDismissRequest = savedJourneysViewModel::toggleShowDeleteDialog,
                onConfirmRequest = {
                    val successful = savedJourneysViewModel.deleteSavedJourney()
                    if (successful) {
                        savedJourneysViewModel.toggleShowDeleteDialog()
                        Toast.makeText(BusApplication.instance, "Successful deletion", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(BusApplication.instance, "Incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            savedJourneysViewModel.toggleShowDeleteDialog()
        }
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    savedJourneysViewModel.setSelectedSavedJourney(null)
                    savedJourneysViewModel.toggleShowAddDialog()
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add journey")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            SavedJourneysList(
                navController = navController,
                uiState = savedJourneysUIState,
                onEdit = {
                    savedJourneysViewModel.setSelectedSavedJourney(it)
                    savedJourneysViewModel.toggleShowAddDialog()
                         },
                onDelete = {
                    savedJourneysViewModel.setSelectedSavedJourney(it)
                    savedJourneysViewModel.toggleShowDeleteDialog()
                }
            )
        }
    }

}

@Composable
private fun SavedJourneysList(
    navController: NavHostController,
    uiState: SavedJourneysUIState,
    onEdit: (BusJourneyListInfo) -> Unit,
    onDelete: (BusJourneyListInfo) -> Unit
) {

    val data = uiState.savedJourneys

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp)
    ) {
        items(data) { savedJourney ->
            SavedJourneyInformation(
                navController = navController,
                data = savedJourney,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }

}

@Composable
private fun SavedJourneyInformation(
    navController: NavHostController,
    data: BusJourneyListInfo,
    onEdit: (BusJourneyListInfo) -> Unit,
    onDelete: (BusJourneyListInfo) -> Unit
) {

    Row(
        modifier = Modifier
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
            .clickable {
                navigateToSavedJourney(
                    navController = navController,
                    journeyID = data.journeyID
                )
            }
    ) {
        Text(
            text = data.description,
            modifier = Modifier.weight(4f)
        )
        Icon(
            modifier = Modifier.weight(1f)
                .clickable { onEdit(data) }
                .align(Alignment.CenterVertically),
            imageVector = Icons.Filled.Edit,
            contentDescription = "Edit"
        )
        Icon(
            modifier = Modifier.weight(1f)
                .clickable { onDelete(data) }
                .align(Alignment.CenterVertically),
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete"
        )
    }

}

@Composable
private fun TextFieldDialog(
    value: String,
    title: String = "",
    label: String = "",
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = value,
                singleLine = true,
                onValueChange = onValueChange,
                label = { Text(text = label) },
                isError = isError,
                keyboardActions = KeyboardActions(onDone = { onConfirmRequest() }),
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            )
        }
    }
}

private fun navigateToSavedJourney(
    navController: NavHostController,
    journeyID: String
) {
    navController.navigate(Pages.SavedJourney.withText(journeyID))
}