package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime

@Serializable
data class Bus (
    @SerialName("OriginCode") val originCode: String = "",
    @SerialName("DestinationCode") val destinationCode: String = "",

    @Serializable(with = LocalDateTimeDeserializer::class)
    @SerialName("EstimatedArrival") val estimatedArrival: LocalDateTime = LocalDateTime.MIN,

    @SerialName("Monitored") val monitored: Int = 0,

    @Serializable(with = DoubleStringSerializer::class)
    @SerialName("Latitude") val latitude: Double = 0.0,

    @Serializable(with = DoubleStringSerializer::class)
    @SerialName("Longitude") val longitude: Double = 0.0,

    @SerialName("VisitNumber") val visitNumber: String = "1",
    @SerialName("Load") val load: String = "",
    @SerialName("Feature") val feature: String = "",
    @SerialName("Type") val type: String = ""
): HasCoordinates {

    suspend fun getDestinationBusStopInfo(busRepository: BusRepository): BusStopInfo? {
        return busRepository.getBusStop(destinationCode)
    }

    suspend fun getOriginBusStopInfo(busRepository: BusRepository): BusStopInfo? {
        return busRepository.getBusStop(originCode)
    }

    fun isValid(): Boolean {
        return this.estimatedArrival != LocalDateTime.MIN
    }

    fun isLive(): Boolean {
        return this.isValid() && this.monitored == 1
    }

    fun isLoop(): Boolean {
        return this.isValid() && this.originCode == this.destinationCode
    }

    fun getDuration() : Int {
        val duration: Int = Duration.between(LocalDateTime.now(), this.estimatedArrival).toMinutes().toInt()
        return if (this.isValid()) (if (duration >= 0) duration else 0) else -1
    }

    fun getDistanceFrom(hasCoordinates: HasCoordinates?): Int {
        return if (hasCoordinates != null && isValid() && monitored == 1) {
            this.distanceFromInMetres(hasCoordinates)
        } else {
            -1
        }
    }

    override fun getCoordinates(): Pair<Double, Double> {
        return Pair(latitude, longitude)
    }
}
