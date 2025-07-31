package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class contains information of a bus stop, including the route information of
 * all bus services that serve this stop.
 *
 * @param busStopInfo The bus stop information
 * @param busRoutesInfo The list of route information of all bus services that serve this stop
 */
data class BusStopInfoWithBusRoutesInfo (
    @Embedded val busStopInfo: BusStopInfo,
    @Relation(
        parentColumn = "busStopCode",
        entityColumn = "busStopCode"
    )
    val busRoutesInfo: List<BusRouteInfo>
)