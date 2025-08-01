package com.aiepoissac.busapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.userdata.JourneyInfo
import com.aiepoissac.busapp.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * This class is the View Model factory for the View Model for the Saved Journeys page
 *
 * @param userDataRepository The repository of the user data
 */
class SavedJourneysViewModelFactory(
    private val userDataRepository: UserDataRepository = BusApplication.instance.container.userDataRepository
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SavedJourneysViewModel::class.java)) {
            SavedJourneysViewModel(userDataRepository = userDataRepository) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}

/**
 * This class is the View Model for the Saved Journeys page
 *
 * @param userDataRepository The repository of the user data
 */
class SavedJourneysViewModel(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedJourneysUIState())
    val uiState = _uiState.asStateFlow()

    init {
        refreshList()
    }

    /**
     * Reload the Journeys list displayed.
     */
    private fun refreshList() {
        viewModelScope.launch {
            _uiState.update {
                SavedJourneysUIState(
                    savedJourneys = userDataRepository.getAllJourneys()
                )
            }
        }
    }

    /**
     * Add a Journey to the database using the description input value and a randomly generated ID
     */
    private fun addSavedJourney() {
        viewModelScope.launch {
            userDataRepository.insertJourney(
                JourneyInfo(
                    journeyID = generateRandomString(length = 8),
                    description = uiState.value.descriptionInput
                )
            )
            refreshList()
        }
    }

    /**
     * Update the description of the currently selected Journey to the database
     * using the description input value.
     */
    fun updateSavedJourney() {
        val savedJourney = uiState.value.selectedSavedJourney
        if (savedJourney != null) {
            viewModelScope.launch {
                userDataRepository.updateJourney(
                    journeyInfo = savedJourney.copy(description = uiState.value.descriptionInput)
                )
                refreshList()
            }

        } else {
            addSavedJourney()
        }
    }

    /**
     * Delete the currently selected Journey from the database.
     */
    fun deleteSavedJourney(): Boolean {
        val savedJourney = uiState.value.selectedSavedJourney
        if (savedJourney != null && uiState.value.descriptionInput == savedJourney.description) {
            viewModelScope.launch {
                userDataRepository.deleteJourney(savedJourney)
                userDataRepository.deleteAllJourneySegments(savedJourney.journeyID)
                refreshList()
            }
            return true
        } else {
            return false
        }
    }

    /**
     * Selects a Journey to update or delete.
     *
     * @param savedJourney The Journey to be selected, or null if no Journey is to be selected
     */
    fun setSelectedSavedJourney(savedJourney: JourneyInfo?) {
        _uiState.update {
            it.copy(selectedSavedJourney = savedJourney)
        }
    }

    /**
     * Toggles whether Add Saved Journey dialog should be shown.
     */
    fun toggleShowAddDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = !uiState.value.showAddDialog
            )
        }
    }

    /**
     * Updates the description input field for Add Saved Journey dialog.
     *
     * @param description The updated description
     */
    fun updateDescriptionInput(description: String) {
        _uiState.update {
            it.copy(descriptionInput = description)
        }
    }

    /**
     * Toggles whether Delete Saved Journey dialog should be shown.
     */
    fun toggleShowDeleteDialog() {
        _uiState.update {
            it.copy(showDeleteDialog = !uiState.value.showDeleteDialog)
        }
    }

}

/**
 * Generates a random alphanumeric String.
 *
 * @param length Length of the String
 */
fun generateRandomString(length: Int): String {
    val validChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return List(length) { validChars.random() }.joinToString(separator = "")
}