package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

data class BusServiceInfoWithBusStopInfo (
    @Embedded val busServiceInfo: BusServiceInfo,
    @Relation(
        parentColumn = "originCode",
        entityColumn = "busStopCode"
    ) val originBusStopInfo: BusStopInfo,
    @Relation(
        parentColumn = "destinationCode",
        entityColumn = "busStopCode"
    ) val destinationBusStopInfo: BusStopInfo
)