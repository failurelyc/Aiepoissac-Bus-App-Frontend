package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Serializable
data class PlannedBusRoutesInfo(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<PlannedBusRouteInfo>
)

/**
 * This class contains information of the nth stop of a future bus service, including the
 * first/last bus timings for this stop.
 *
 * @param serviceNo The bus service number
 * @param operator Operator for this bus service
 * @param direction The direction in which the bus travels (1 or 2), loop services only have 1 direction
 * @param stopSequence The i-th bus stop for this route
 * @param busStopCode The unique 5-digit identifier for this physical bus stop
 * @param distance Distance travelled by bus from starting location to this bus stop (in kilometres)
 * @param wdFirstBus Scheduled arrival of first bus on weekdays
 * @param wdLastBus Scheduled arrival of last bus on weekdays
 * @param satFirstBus Scheduled arrival of first bus on Saturdays
 * @param satLastBus Scheduled arrival of last bus on Saturdays
 * @param sunFirstBus Scheduled arrival of first bus on Sundays
 * @param sunLastBus Scheduled arrival of last bus on Sundays
 * @param effectiveDate The date when the new/update bus routes will take effect.
 */
@Entity(
    tableName = "Planned_Bus_Routes_Table",
    primaryKeys = ["serviceNo", "direction", "stopSequence"]
)
@Serializable
data class PlannedBusRouteInfo(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String = "",
    @SerialName("Direction") val direction: Int,
    @SerialName("StopSequence") val stopSequence: Int,
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("Distance") val distance: Double,
    @SerialName("WD_FirstBus") val wdFirstBus: String = "",
    @SerialName("WD_LastBus") val wdLastBus: String = "",
    @SerialName("SAT_FirstBus") val satFirstBus: String = "",
    @SerialName("SAT_LastBus") val satLastBus: String = "",
    @SerialName("SUN_FirstBus") val sunFirstBus: String = "",
    @SerialName("SUN_LastBus") val sunLastBus: String = "",
    @SerialName("EffectiveDate") val effectiveDate: String
) {

    /**
     * Get the effective date as a LocalDateTime Object
     *
     * @return The effective date
     */
    fun getEffectiveDate(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm:ssXX")
        return LocalDateTime.parse(effectiveDate, formatter)
    }
}

