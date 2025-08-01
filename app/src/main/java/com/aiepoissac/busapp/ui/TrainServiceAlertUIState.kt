package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.mrtstation.AffectedSegment
import com.aiepoissac.busapp.data.mrtstation.FacilityMaintenance
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import com.aiepoissac.busapp.data.mrtstation.TrainServiceAlerts

/**
 * This class stores the UI state for the Train Service Alert page.
 *
 * @param facilitiesMaintenance The facilities maintenance data, or null if failed to obtain the data
 * @param trainServiceAlerts The train service alerts data, or null if failed to obtain the data
 * @param showFacilitiesMaintenance Whether facilities maintenance should be shown
 * @param showTrainServiceAlerts Whether train service alerts should be shown
 */
data class TrainServiceAlertUIState(
    val facilitiesMaintenance: List<Pair<MRTStation, FacilityMaintenance>>? = null,
    val trainServiceAlerts: TrainServiceAlerts? = null,
    val affectedSegmentsWithMRTStationList: List<Pair<AffectedSegment, List<MRTStation>>>? = null,
    val showFacilitiesMaintenance: Boolean = false,
    val showTrainServiceAlerts: Boolean = true
)
