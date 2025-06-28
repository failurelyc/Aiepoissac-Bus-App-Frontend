package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.userdata.BusJourneyListInfo

data class SavedJourneysUIState (
    val savedJourneys: List<BusJourneyListInfo> = listOf(),
    val showAddDialog: Boolean = false,
    val descriptionInput: String = "",
    val selectedSavedJourney: BusJourneyListInfo? = null,
    val showDeleteDialog: Boolean = false
)