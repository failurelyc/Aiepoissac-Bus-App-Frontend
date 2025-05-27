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

)

fun truncateTillBusStop(
    route: List<BusRouteInfoWithBusStopInfo>,
    stopSequence: Int,
    truncateLoop: Boolean,
    adjustStopSequence: Boolean = true
): List<BusRouteInfoWithBusStopInfo> {
    var truncatedRoute = route
        .dropWhile { it.busRouteInfo.stopSequence < stopSequence }

    if (adjustStopSequence) {
        truncatedRoute = removeStopSequenceOffset(truncatedRoute)
    }

    if (!truncateLoop) {
        return truncatedRoute
    } else {
        return truncateLoopRoute(route = truncatedRoute)
    }
}

fun removeStopSequenceOffset(truncatedRoute: List<BusRouteInfoWithBusStopInfo>):
        List<BusRouteInfoWithBusStopInfo> {
    val distanceOffset = truncatedRoute.first().busRouteInfo.distance
    val sequenceOffset = truncatedRoute.first().busRouteInfo.stopSequence
    return truncatedRoute
        .map { stop ->
            val adjustedSequence = stop.busRouteInfo.stopSequence - sequenceOffset
            val adjustedDistance = stop.busRouteInfo.distance - distanceOffset
            stop.copy(busRouteInfo = stop.busRouteInfo.copy(
                stopSequence = adjustedSequence,
                distance = adjustedDistance)
            )
        }
}

fun truncateLoopRoute(route: List<BusRouteInfoWithBusStopInfo>):
        List<BusRouteInfoWithBusStopInfo> {
    val seenBusStopCodes: HashSet<String> = HashSet()
    var lastSeen = ""
    return route
        .takeWhile {
            val thisStop = it.busStopInfo.busStopCode
            lateinit var oppositeStop: String
            if (thisStop.last() == '9') {
                oppositeStop = thisStop.substring(0, thisStop.length - 1) + "1"
            } else if (thisStop.last() == '1') {
                oppositeStop = thisStop.substring(0, thisStop.length - 1) + "9"
            } else {
                return@takeWhile true
            }
            if (seenBusStopCodes.contains(oppositeStop) && oppositeStop != lastSeen) {
                return@takeWhile false
            }
            seenBusStopCodes.add(thisStop)
            lastSeen = thisStop
            return@takeWhile true
        }
}