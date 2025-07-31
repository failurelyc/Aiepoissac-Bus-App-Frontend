package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class contains information of the nth stop of a bus service,
 * and information of the bus stop
 *
 * @param busRouteInfo The route information of a bus service at this bus stop
 * @param busStopInfo The bus stop information
 */
data class BusRouteInfoWithBusStopInfo (
    @Embedded val busRouteInfo: BusRouteInfo,
    @Relation(
        parentColumn = "busStopCode",
        entityColumn = "busStopCode"
    )
    val busStopInfo: BusStopInfo
) {
    override fun toString(): String {
        return "${busRouteInfo.stopSequence} $busStopInfo"
    }
}