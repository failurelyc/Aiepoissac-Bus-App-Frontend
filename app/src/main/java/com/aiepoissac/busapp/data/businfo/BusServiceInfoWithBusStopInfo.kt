package com.aiepoissac.busapp.data.businfo

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class contains information of a bus service, including information
 * of the origin and destination bus stops.
 *
 * @param busServiceInfo The bus service information
 * @param originBusStopInfo The origin bus stop
 * @param destinationBusStopInfo The destination bus stop
 */
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