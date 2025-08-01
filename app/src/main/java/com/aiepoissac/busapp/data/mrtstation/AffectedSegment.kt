package com.aiepoissac.busapp.data.mrtstation

import com.aiepoissac.busapp.data.businfo.BusRepository
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class contains information of a segment in the MRT network that is disrupted/delayed.
 *
 * @param line Train network line affected
 * @param direction Indicates direction of service unavailability on the affected line:
 * • Both • (towards station name)
 * @param stations Indicates the list of affected station codes on the affected line.
 * @param freePublicBusStationCodes Indicates the list of affected station codes where free
 * boarding onto normal public bus services are available.
 * • (station code) • Free bus service island wide
 * @param freeMRTShuttleStationCodes Indicates the list of affected stations where free
 * MRT shuttle services are available.
 * • (station code) • EW21|CC22,EW23,EW24|NS1,EW27;NS9,NS13,NS16,NS17|CC15;EW8|CC9,EW5,EW2;NS1|EW24,NS4|BP1
 * @param mrtShuttleDirection Indicates the direction of free MRT shuttle services available:
 * • Both • (towards station name)
 */
@Serializable
data class AffectedSegment(
    @SerialName("Line") val line: String,
    @SerialName("Direction") val direction: String,
    @SerialName("Stations") val stations: String,
    @SerialName("FreePublicBus") val freePublicBusStationCodes: String,
    @SerialName("FreeMRTShuttle") val freeMRTShuttleStationCodes: String,
    @SerialName("MRTShuttleDirection") val mrtShuttleDirection: String
) {

    /**
     * Get the MRT stations within the affected segment, including the MRT station location.
     *
     * @param busRepository The repository of the bus data
     * @return A list of the MRT stations within the affected segment
     */
    suspend fun getAffectedStations(busRepository: BusRepository): List<MRTStation> {
        return stations.split(delimiters = charArrayOf(','))
            .map { busRepository.getMRTStation(it) }
    }

    /**
     * Get the MRT stations within the affected segment with free public bus services,
     * including the MRT station location.
     *
     * @param busRepository The repository of the bus data
     * @return A list of the MRT stations with free public bus services
     */
    suspend fun getAffectedStationsWithFreeBus(busRepository: BusRepository): List<MRTStation> {
        return if (freePublicBusStationCodes == "Free bus service island-wide") {
            busRepository.getAllMRTStations()
        } else {
            freePublicBusStationCodes.split(delimiters = charArrayOf(','))
                .map { busRepository.getMRTStation(it) }
        }
    }

}