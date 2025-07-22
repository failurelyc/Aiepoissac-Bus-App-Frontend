package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.mrtstation.AffectedSegment
import com.aiepoissac.busapp.data.mrtstation.FacilityMaintenance
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import com.aiepoissac.busapp.data.mrtstation.TrainServiceAlerts

data class TrainServiceAlertUIState(
    val facilitiesMaintenance: List<Pair<MRTStation, FacilityMaintenance>>? = null,
    val trainServiceAlerts: TrainServiceAlerts? = null,
    val affectedSegmentsWithMRTStationList: List<Pair<AffectedSegment, List<MRTStation>>>? = null,
    val showFacilitiesMaintenance: Boolean = false,
    val showTrainServiceAlerts: Boolean = true
)
