package com.aiepoissac.busapp.data.mrtstation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FacilitiesMaintenance(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<FacilityMaintenance>
)

/**
 * This class contains information of lift that is under maintenance.
 *
 * @param line Code of train network line
 * @param stationCode Code of train station.
 * @param stationName Name of train station
 * @param liftID ID of the lift which is currently under maintenance.
 * @param liftDescription Detailed description of the lift which is currently under maintenance.
 */
@Serializable
data class FacilityMaintenance(
    @SerialName("Line") val line: String,
    @SerialName("StationCode") val stationCode: String,
    @SerialName("StationName") val stationName: String,
    @SerialName("LiftID") val liftID: String,
    @SerialName("LiftDesc") val liftDescription: String
)
