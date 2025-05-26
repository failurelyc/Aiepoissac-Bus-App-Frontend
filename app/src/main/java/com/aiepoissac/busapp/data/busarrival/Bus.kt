package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
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
): HasCoordinates {
    fun isValid(): Boolean {
        return this.estimatedArrival != LocalDateTime.MIN
    }

    fun isLive(): Boolean {
        return this.isValid() && this.monitored == 1
    }

    fun getDuration() : Int {
        val duration: Int = Duration.between(LocalDateTime.now(), this.estimatedArrival).toMinutes().toInt()
        return if (this.isValid()) (if (duration >= 0) duration else 0) else -1
    }

    fun getDistanceFrom(busStopInfo: BusStopInfo?): Int {
        return if (busStopInfo != null && isValid() && monitored == 1) {
            this.distanceFromInMetres(busStopInfo)
        } else {
            -1
        }
    }

    override fun getCoordinates(): Pair<Double, Double> {
        return Pair(latitude, longitude)
    }
}
