package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusRoutesInfo(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BusRouteInfo>
)

@Entity(
    tableName = "Bus_Routes_Table",
    primaryKeys = ["serviceNo", "direction", "stopSequence"]
)
@Serializable
data class BusRouteInfo (
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String,
    @SerialName("Direction") val direction: Int,
    @SerialName("StopSequence") val stopSequence: Int,
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("Distance") val distance: Double,
    @SerialName("WD_FirstBus") val wdFirstBus: String,
    @SerialName("WD_LastBus") val wdLastBus: String,
    @SerialName("SAT_FirstBus") val satFirstBus: String,
    @SerialName("SAT_LastBus") val satLastBus: String,
    @SerialName("SUN_FirstBus") val sunFirstBus: String,
    @SerialName("SUN_LastBus") val sunLastBus: String

) {

}