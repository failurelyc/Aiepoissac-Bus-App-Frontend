package com.aiepoissac.busapp.data.mrtstation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FacilitiesMaintenance(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<FacilityMaintenance>
)

@Serializable
data class FacilityMaintenance(
    @SerialName("Line") val line: String,
    @SerialName("StationCode") val stationCode: String,
    @SerialName("StationName") val stationName: String,
    @SerialName("LiftID") val liftID: String,
    @SerialName("LiftDesc") val liftDescription: String
)
