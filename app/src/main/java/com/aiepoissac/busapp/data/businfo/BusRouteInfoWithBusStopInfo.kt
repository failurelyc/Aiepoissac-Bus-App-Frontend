package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

data class BusRouteInfoWithBusStopInfo (
    @Embedded val busRouteInfo: BusRouteInfo,
    @Relation(
        parentColumn = "busStopCode",
        entityColumn = "busStopCode"
    )
    val busStopInfo: BusStopInfo
)