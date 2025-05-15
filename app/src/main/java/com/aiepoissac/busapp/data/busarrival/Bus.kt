package com.aiepoissac.busapp.data.busarrival

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Bus (
    @SerialName("OriginCode") val originCode: String,
    @SerialName("DestinationCode") val destinationCode: String,

    @Serializable(with = LocalDateTimeDeserializer::class)
    @SerialName("EstimatedArrival") val estimatedArrival: LocalDateTime,

    @SerialName("Monitored") val monitored: Int,

    @Serializable(with = DoubleStringSerializer::class)
    @SerialName("Latitude") val latitude: Double,

    @Serializable(with = DoubleStringSerializer::class)
    @SerialName("Longitude") val longitude: Double,

    @SerialName("VisitNumber") val visitNumber: String,
    @SerialName("Load") val load: String,
    @SerialName("Feature") val feature: String,
    @SerialName("Type") val type: String
) {
    fun isValid(): Boolean {
        return this.estimatedArrival != LocalDateTime.MIN
    }
}
