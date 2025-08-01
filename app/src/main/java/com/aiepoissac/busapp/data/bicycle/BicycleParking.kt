package com.aiepoissac.busapp.data.bicycle

import com.aiepoissac.busapp.data.HasCoordinates
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class represents all bicycle parking locations near a point
 *
 * @param metadata
 * @param value The list of bicycle parking locations.
 */
@Serializable
data class BicycleParkingList(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BicycleParking>
)

/**
 * This class represents a bicycle parking location.
 *
 * @param description Brief description of bicycle parking location.
 * @param latitude Latitude map coordinates of bicycle parking location
 * @param longitude Longitude map coordinates of bicycle parking location.
 * @param rackType Type of bicycle parking facility.
 * @param rackCount Total number of bicycle parking lots.
 * @param shelterIndicator Indicate whether the bicycle parking lots are sheltered.
 */
@Serializable
data class BicycleParking(
    @SerialName("Description") val description: String,
    @SerialName("Latitude") override val latitude: Double,
    @SerialName("Longitude") override val longitude: Double,
    @SerialName("RackType") val rackType: String,
    @SerialName("RackCount") val rackCount: Int,
    @SerialName("ShelterIndicator") val shelterIndicator: String

): HasCoordinates {

    /**
     * Checks if the bicycle parking is sheltered.
     * @return True if the bicycle parking is sheltered, false otherwise.
     */
    fun hasShelter(): Boolean {
        return shelterIndicator == "Y"
    }

}
