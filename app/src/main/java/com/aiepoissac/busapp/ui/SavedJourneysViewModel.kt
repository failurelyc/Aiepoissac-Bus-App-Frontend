package com.aiepoissac.busapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.userdata.BusJourneyListInfo
import com.aiepoissac.busapp.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class SavedJourneysViewModel(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedJourneysUIState())
    val uiState = _uiState.asStateFlow()

    init {
        refreshList()
    }

    private fun refreshList() {
        viewModelScope.launch {
            _uiState.update {
                SavedJourneysUIState(
                    savedJourneys = userDataRepository.getAllBusJourneyListInfo()
                )
            }
        }
    }

    private fun addSavedJourney() {
        viewModelScope.launch {
            userDataRepository.insertBusJourneyListInfo(
                BusJourneyListInfo(
                    journeyID = generateRandomString(length = 8),
                    description = uiState.value.descriptionInput
                )
            )
            refreshList()
        }
    }

    fun updateSavedJourney() {
        val savedJourney = uiState.value.selectedSavedJourney
        if (savedJourney != null) {
            viewModelScope.launch {
                userDataRepository.updateBusJourneyListInfo(
                    busJourneyListInfo = savedJourney.copy(description = uiState.value.descriptionInput)
                )
                refreshList()
            }

        } else {
            addSavedJourney()
        }
    }

    fun deleteSavedJourney(): Boolean {
        val savedJourney = uiState.value.selectedSavedJourney
        if (savedJourney != null && uiState.value.descriptionInput == savedJourney.description) {
            viewModelScope.launch {
                userDataRepository.deleteBusJourneyListInfo(savedJourney)
                userDataRepository.deleteBusJourneyList(savedJourney.journeyID)
                refreshList()
            }
            return true
        } else {
            return false
        }
    }

    fun setSelectedSavedJourney(savedJourney: BusJourneyListInfo?) {
        _uiState.update {
            it.copy(selectedSavedJourney = savedJourney)
        }
    }

    fun toggleShowAddDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = !uiState.value.showAddDialog
            )
        }
    }

    fun updateDescriptionInput(description: String) {
        _uiState.update {
            it.copy(descriptionInput = description)
        }
    }

    fun toggleShowDeleteDialog() {
        _uiState.update {
            it.copy(showDeleteDialog = !uiState.value.showDeleteDialog)
        }
    }

}

private fun generateRandomString(length: Int): String {
    val validChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return List(length) { validChars.random() }.joinToString(separator = "")
}