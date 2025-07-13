package com.aiepoissac.busapp.data.bicycle

import com.aiepoissac.busapp.data.HasCoordinates
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BicycleParkingList(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BicycleParking>
)

@Serializable
data class BicycleParking(
    @SerialName("Description") val description: String,
    @SerialName("Latitude") override val latitude: Double,
    @SerialName("Longitude") override val longitude: Double,
    @SerialName("RackType") val rackType: String,
    @SerialName("RackCount") val rackCount: Int,
    @SerialName("ShelterIndicator") val shelterIndicator: String

): HasCoordinates {

    fun hasShelter(): Boolean {
        return shelterIndicator == "Y"
    }

}
