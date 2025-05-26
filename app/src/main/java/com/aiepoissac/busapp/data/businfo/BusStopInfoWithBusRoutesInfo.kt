package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

data class BusStopInfoWithBusRoutesInfo (
    @Embedded val busStopInfo: BusStopInfo,
    @Relation(
        parentColumn = "busStopCode",
        entityColumn = "busStopCode"
    )
    val busRoutesInfo: List<BusRouteInfo>
)