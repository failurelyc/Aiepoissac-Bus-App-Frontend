package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.userdata.JourneyInfo

data class SavedJourneysUIState (
    val savedJourneys: List<JourneyInfo> = listOf(),
    val showAddDialog: Boolean = false,
    val descriptionInput: String = "",
    val selectedSavedJourney: JourneyInfo? = null,
    val showDeleteDialog: Boolean = false
)