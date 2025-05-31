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

fun isLoop(route: List<BusRouteInfoWithBusStopInfo>): Boolean {
    return route.first().busStopInfo.description == route.last().busStopInfo.description ||
            oppositeBusStopCode(route.first().busStopInfo.busStopCode) == route.last().busStopInfo.busStopCode
}

fun truncateTillBusStop(
    route: List<BusRouteInfoWithBusStopInfo>,
    stopSequence: Int,
    adjustStopSequence: Boolean = true
): List<BusRouteInfoWithBusStopInfo> {
    var visitedFirstStopAgain = -1

    var truncatedRoute = route
        .dropWhile {
            if ((it.busStopInfo.description == route.first().busStopInfo.description ||
                    it.busStopInfo.busStopCode == oppositeBusStopCode(route.first().busStopInfo.busStopCode)) &&
                visitedFirstStopAgain == -1 &&
                it != route.first() && it != route.last())
                visitedFirstStopAgain = it.busRouteInfo.stopSequence
            return@dropWhile it.busRouteInfo.stopSequence < stopSequence }

    if (visitedFirstStopAgain >= 0) { //bus service is a dual loop
        truncatedRoute = truncatedRoute +
                addStopSequenceOffset(route, truncatedRoute.last())
    }

    if (adjustStopSequence) {
        truncatedRoute = removeStopSequenceOffset(truncatedRoute)
    }

    if (!isLoop(route)) {
        return truncatedRoute
    } else {
        return truncateLoopRoute(route = truncatedRoute).second
    }
}

private fun addStopSequenceOffset(
    route: List<BusRouteInfoWithBusStopInfo>,
    firstStop: BusRouteInfoWithBusStopInfo
): List<BusRouteInfoWithBusStopInfo> {
    return route.map { stop ->
        stop.copy(busRouteInfo = stop.busRouteInfo.copy(
            stopSequence = stop.busRouteInfo.stopSequence + firstStop.busRouteInfo.stopSequence,
            distance = stop.busRouteInfo.distance + firstStop.busRouteInfo.distance)
        )
    }
}

private fun removeStopSequenceOffset(truncatedRoute: List<BusRouteInfoWithBusStopInfo>):
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

fun truncateLoopRoute(
    route: List<BusRouteInfoWithBusStopInfo>,
    after: Boolean = false
): Pair<Int, List<BusRouteInfoWithBusStopInfo>> {
    val seenBusStopCodes: HashMap<String, Int> = HashMap()
    var lastSeen = ""
    val truncatedRoute = route
        .takeWhile {
            val thisStop = it.busStopInfo.busStopCode
            val oppositeStop: String = oppositeBusStopCode(thisStop)
            if (oppositeStop == thisStop) {
                return@takeWhile true
            }

            if (seenBusStopCodes.contains(oppositeStop) && oppositeStop != lastSeen) {
                lastSeen = oppositeStop
                return@takeWhile false
            }
            seenBusStopCodes.put(thisStop, it.busRouteInfo.stopSequence)
            lastSeen = thisStop
            return@takeWhile true
        }
    if (!after) {
        return Pair(0, truncatedRoute)
    } else {
        val stopSequenceOffset = (seenBusStopCodes.get(lastSeen)?: 0) + 1
        return Pair(
            first = stopSequenceOffset,
            second = truncateTillBusStop(
                route = route,
                stopSequence = stopSequenceOffset
            )
        )
    }
}

private fun oppositeBusStopCode(busStopCode: String): String {
    return if (busStopCode.last() == '9') {
        busStopCode.substring(0, busStopCode.length - 1) + '1'
    } else if (busStopCode.last() == '1') {
        busStopCode.substring(0, busStopCode.length - 1) + '9'
    } else {
        busStopCode
    }
}


