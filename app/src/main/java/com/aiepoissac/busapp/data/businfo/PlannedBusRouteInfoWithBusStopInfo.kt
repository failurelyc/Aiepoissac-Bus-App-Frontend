package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class contains information of the nth stop of a future bus service,
 * including information of the bus stop if it exists
 *
 * @param plannedBusRouteInfo The planned route information of a bus service at this bus stop
 * @param busStopInfo The bus stop information, or null if the bus stop is a future bus stop
 */
data class PlannedBusRouteInfoWithBusStopInfo (
    @Embedded val plannedBusRouteInfo: PlannedBusRouteInfo,
    @Relation(
        parentColumn = "busStopCode",
        entityColumn = "busStopCode"
    )
    val busStopInfo: BusStopInfo?

) {
    override fun toString(): String {
        return if (busStopInfo != null) {
            "${plannedBusRouteInfo.stopSequence} $busStopInfo"
        } else {
            "${plannedBusRouteInfo.stopSequence} ${plannedBusRouteInfo.busStopCode} Future bus stop"
        }
    }
}