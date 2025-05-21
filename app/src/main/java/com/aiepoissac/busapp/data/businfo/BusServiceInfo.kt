package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BusServicesInfo(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BusServiceInfo>
)

@Entity(
    tableName = "Bus_Services_Table",
    primaryKeys = ["serviceNo", "direction"])
@Serializable
data class BusServiceInfo(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String,
    @SerialName("Direction") val direction: Int,
    @SerialName("Category") val category: String,
    @SerialName("OriginCode") val originCode: String,
    @SerialName("DestinationCode") val destinationCode: String,
    @SerialName("AM_Peak_Freq") val amPeakFreq: String,
    @SerialName("AM_Offpeak_Freq") val amOffPeakFreq: String,
    @SerialName("PM_Peak_Freq") val pmPeakFreq: String,
    @SerialName("PM_Offpeak_Freq") val pmOffPeakFreq: String,
    @SerialName("LoopDesc") val loopDesc: String
) {
    fun isLoop(): Boolean {
        return loopDesc.isNotEmpty();
    }
}

