package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.userdata.JourneyInfo

/**
 * This class stores the UI state for the Saved Journeys page.
 *
 * @param savedJourneys The list of all Saved Journeys
 * @param showAddDialog Whether the Add Saved Journey Dialog is shown
 * @param descriptionInput The value of the description input in the Add Saved Journey Dialog
 * @param selectedSavedJourney The currently selected Saved Journey for updating or deletion
 * @param showDeleteDialog Whether the Delete Saved Journey Dialog is shown
 */
data class SavedJourneysUIState (
    val savedJourneys: List<JourneyInfo> = listOf(),
    val showAddDialog: Boolean = false,
    val descriptionInput: String = "",
    val selectedSavedJourney: JourneyInfo? = null,
    val showDeleteDialog: Boolean = false
)