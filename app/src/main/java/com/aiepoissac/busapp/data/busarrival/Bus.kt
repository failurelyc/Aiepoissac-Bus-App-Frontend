package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime

/**
 * This class represents a specific bus operating on a BusService at a BusStop.
 * Instances of this class do not represent a valid bus if estimatedArrival is zero.
 *
 * @param originCode Reference code of the first bus stop where this bus started its service
 * @param destinationCode Reference code of the last bus stop where this bus will terminate its service
 * @param estimatedArrival Date-time of this bus’ estimated time of arrival,
 * expressed in the UTC standard, GMT+8 for Singapore Standard Time (SST).
 * @param monitored Indicates if the bus arrival time is based on the
 * schedule from operators. • 0 (Value from EstimatedArrival is based on schedule)
 * • 1 (Value from EstimatedArrival is estimated based on bus location)
 * @param latitude Current estimated location coordinates of this bus at point of published data.
 * Only valid if the bus is live
 * @param longitude Current estimated location coordinates of this bus at point of published data.
 * Only valid if the bus is live
 * @param visitNumber Ordinal value of the nth visit of this vehicle at this bus stop; 1=1st visit, 2=2nd visit
 * @param load Current bus occupancy / crowding level:
 * • SEA (for Seats Available)
 * • SDA (for Standing Available)
 * • LSD (for Limited Standing)
 * @param feature Indicates if bus is wheel-chair accessible:
 * • WAB
 * • (empty / blank)
 * @param type Vehicle type:
 * • SD (for Single Deck)
 * • DD (for Double Deck)
 * • BD (for Bendy)
 */
@Serializable
data class Bus (
    @SerialName("OriginCode") val originCode: String = "",
    @SerialName("DestinationCode") val destinationCode: String = "",

    @Serializable(with = LocalDateTimeDeserializerForBusArrivals::class)
    @SerialName("EstimatedArrival") val estimatedArrival: LocalDateTime = LocalDateTime.MIN,

    @SerialName("Monitored") val monitored: Int = 0,

    @Serializable(with = DoubleStringSerializer::class)
    @SerialName("Latitude") override val latitude: Double = 0.0,

    @Serializable(with = DoubleStringSerializer::class)
    @SerialName("Longitude") override val longitude: Double = 0.0,

    @SerialName("VisitNumber") val visitNumber: String = "1",
    @SerialName("Load") val load: String = "",
    @SerialName("Feature") val feature: String = "",
    @SerialName("Type") val type: String = ""
): HasCoordinates {

    /**
     * Get the BusStopInfo of the destination of this bus
     *
     * @param busRepository the repository of the bus data
     * @return the BusStopInfo of the destination of this bus
     */
    suspend fun getDestinationBusStopInfo(busRepository: BusRepository): BusStopInfo? {
        return busRepository.getBusStop(destinationCode)
    }

    /**
     * Get the BusStopInfo of the origin of this bus. This may not be the first stop of
     * the bus route if the bus starts service mid route.
     *
     * @param busRepository the repository of the bus data
     * @return busStopInfo of the origin of this bus
     */
    suspend fun getOriginBusStopInfo(busRepository: BusRepository): BusStopInfo? {
        return busRepository.getBusStop(originCode)
    }

    /**
     * Check if the bus data obtained from LTA API is valid and not "filler data"
     *
     * @return true if this bus is valid
     */
    fun isValid(): Boolean {
        return this.estimatedArrival != LocalDateTime.MIN
    }

    /**
     * Check if the bus have started service
     *
     * @return true if the bus is live
     */
    fun isLive(): Boolean {
        return this.isValid() && this.monitored == 1
    }

    fun isLoop(): Boolean {
        return this.isValid() && this.originCode == this.destinationCode
    }

    /**
     * Get the duration between the time the function is called, and the estimatedArrival
     *
     * @return the duration in minutes
     */
    fun getDuration(): Int {
        val duration: Int = Duration.between(LocalDateTime.now(), this.estimatedArrival).toMinutes().toInt()
        return if (this.isValid()) (if (duration >= 0) duration else 0) else -1
    }

}
