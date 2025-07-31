package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BusServicesInfo(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BusServiceInfo>
)

/**
 * This class contains information of a bus service,
 * including: first stop, last stop, peak/off peak frequency of dispatch
 *
 * @param serviceNo The bus service number
 * @param operator Operator for this bus service
 * @param direction The direction in which the bus travels (1 or 2), loop services only have 1 direction
 * @param category Category of the bus service
 * @param originCode Bus stop code for first bus stop
 * @param destinationCode Bus stop code for last bus stop
 * @param amPeakFreq Freq of dispatch for AM Peak 0630H - 0830H (range in minutes)
 * @param amOffPeakFreq Freq of dispatch for AM Off-Peak 0831H - 1659H (range in minutes)
 */
@Entity(
    tableName = "Bus_Services_Table",
    primaryKeys = ["serviceNo", "direction"])
@Serializable
data class BusServiceInfo(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String = "",
    @SerialName("Direction") val direction: Int,
    @SerialName("Category") val category: String = "",
    @SerialName("OriginCode") val originCode: String,
    @SerialName("DestinationCode") val destinationCode: String,
    @SerialName("AM_Peak_Freq") val amPeakFreq: String = "",
    @SerialName("AM_Offpeak_Freq") val amOffPeakFreq: String = "",
    @SerialName("PM_Peak_Freq") val pmPeakFreq: String = "",
    @SerialName("PM_Offpeak_Freq") val pmOffPeakFreq: String = "",
    @SerialName("LoopDesc") val loopDesc: String = ""
) {

    /**
     * Checks if the bus service has a loop route
     *
     * @return True if the bus service has a loop route, false otherwise.
     */
    fun isLoop(): Boolean {
        return originCode == destinationCode
                || oppositeBusStopCode(originCode) == destinationCode
    }

    /**
     * Checks if the bus service has a valid AM Peak frequency data
     *
     * @return True if the bus service has a valid AM Peak frequency data, false otherwise
     */
    fun hasAMPeakFrequency(): Boolean {
        return amPeakFreq != "-"
    }

    /**
     * Checks if the bus service has a valid AM Off Peak frequency data
     *
     * @return True if the bus service has a valid AM Off Peak frequency data, false otherwise
     */
    fun hasAMOffPeakFrequency(): Boolean {
        return amOffPeakFreq != "-"
    }

    /**
     * Checks if the bus service has a valid PM Peak frequency data
     *
     * @return True if the bus service has a valid PM Peak frequency data, false otherwise
     */
    fun hasPMPeakFrequency(): Boolean {
        return pmPeakFreq != "-"
    }

    /**
     * Checks if the bus service has a valid PM Off Peak frequency data
     *
     * @return True if the bus service has a valid PM Off Peak frequency data, false otherwise
     */
    fun hasPMOffPeakFrequency(): Boolean {
        return pmOffPeakFreq != "-"
    }

}

