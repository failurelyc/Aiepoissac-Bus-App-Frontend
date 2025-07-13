package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

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