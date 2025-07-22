package com.aiepoissac.busapp.data.mrtstation

import com.aiepoissac.busapp.data.businfo.BusRepository
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AffectedSegment(
    @SerialName("Line") val line: String,
    @SerialName("Direction") val direction: String,
    @SerialName("Stations") val stations: String,
    @SerialName("FreePublicBus") val freePublicBusStationCodes: String,
    @SerialName("FreeMRTShuttle") val freeMRTShuttleStationCodes: String,
    @SerialName("MRTShuttleDirection") val mrtShuttleDirection: String
) {

    suspend fun getAffectedStations(busRepository: BusRepository): List<MRTStation> {
        return stations.split(delimiters = charArrayOf(','))
            .map { busRepository.getMRTStation(it) }
    }

    suspend fun getAffectedStationsWithFreeBus(busRepository: BusRepository): List<MRTStation> {
        return if (freePublicBusStationCodes == "Free bus service island-wide") {
            busRepository.getAllMRTStations()
        } else {
            freePublicBusStationCodes.split(delimiters = charArrayOf(','))
                .map { busRepository.getMRTStation(it) }
        }
    }

}