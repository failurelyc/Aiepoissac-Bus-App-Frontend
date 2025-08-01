package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import com.aiepoissac.busapp.data.HasCoordinates
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.round

@Serializable
data class BusRoutesInfo(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BusRouteInfo>
)

/**
 * This class contains information of the nth stop of a bus service, including the
 * first/last bus timings for this stop.
 *
 * @param serviceNo The bus service number
 * @param operator Operator for this bus service
 * @param direction The direction in which the bus travels (1 or 2), loop services only have 1 direction
 * @param stopSequence The i-th bus stop for this route
 * @param busStopCode The unique 5-digit identifier for this physical bus stop
 * @param distance Distance travelled by bus from starting location to this bus stop (in kilometres)
 * @param wdFirstBus Scheduled arrival of first bus on weekdays
 * @param wdLastBus Scheduled arrival of last bus on weekdays
 * @param satFirstBus Scheduled arrival of first bus on Saturdays
 * @param satLastBus Scheduled arrival of last bus on Saturdays
 * @param sunFirstBus Scheduled arrival of first bus on Sundays
 * @param sunLastBus Scheduled arrival of last bus on Sundays
 */
@Entity(
    tableName = "Bus_Routes_Table",
    primaryKeys = ["serviceNo", "direction", "stopSequence"]
)
@Serializable
data class BusRouteInfo (
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String = "",
    @SerialName("Direction") val direction: Int,
    @SerialName("StopSequence") val stopSequence: Int,
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("Distance") val distance: Double,
    @SerialName("WD_FirstBus") val wdFirstBus: String = "",
    @SerialName("WD_LastBus") val wdLastBus: String = "",
    @SerialName("SAT_FirstBus") val satFirstBus: String = "",
    @SerialName("SAT_LastBus") val satLastBus: String = "",
    @SerialName("SUN_FirstBus") val sunFirstBus: String = "",
    @SerialName("SUN_LastBus") val sunLastBus: String = ""
) {

    /**
     * Checks if the bus is operating on weekdays
     *
     * @return True if the bus have valid first/last bus timings on weekdays
     */
    fun isOperatingOnWeekday(): Boolean {
        return getTime(wdLastBus) != null && getTime(wdFirstBus) != null
    }

    /**
     * Checks if the bus is operating on saturdays
     *
     * @return True if the bus have valid first/last bus timings on saturdays
     */
    fun isOperatingOnSaturday(): Boolean {
        return getTime(satLastBus) != null && getTime(satFirstBus) != null
    }

    /**
     * Checks if the bus is operating on sundays
     *
     * @return True if the bus have valid first/last bus timings on sundays
     */
    fun isOperatingOnSunday(): Boolean {
        return getTime(sunLastBus) != null && getTime(sunFirstBus) != null
    }

    /**
     * Checks if the bus is operating on weekdays at the input time.
     *
     * @param time The time to check
     * @return True if the time is between 30 minutes before the first bus timing
     * and the last bus timing for weekdays, false otherwise
     */
    fun isOperatingOnWeekday(time: LocalTime): Boolean {
        return isOperatingAtThisTime(time, getTime(wdFirstBus), getTime(wdLastBus))
    }

    /**
     * Checks if the bus is operating on saturdays at the input time.
     *
     * @param time The time to check
     * @return True if the time is between 30 minutes before the first bus timing
     * and the last bus timing for saturdays, false otherwise
     */
    fun isOperatingOnSaturday(time: LocalTime): Boolean {
        return isOperatingAtThisTime(time, getTime(satFirstBus), getTime(satLastBus))
    }

    /**
     * Checks if the bus is operating on sundays at the input time.
     *
     * @param time The time to check
     * @return True if the time is between 30 minutes before the first bus timing
     * and the last bus timing for sundays, false otherwise
     */
    fun isOperatingOnSunday(time: LocalTime): Boolean {
        return isOperatingAtThisTime(time, getTime(sunFirstBus), getTime(sunLastBus))
    }

    private fun isOperatingAtThisTime(time: LocalTime, firstBusTiming: LocalTime?, lastBusTiming: LocalTime?): Boolean {
        if (firstBusTiming == null || lastBusTiming == null) {
            return false
        } else {
            val beforeFirstBusTiming = firstBusTiming.minusMinutes(30)
            return when {
                //firstBusTiming == lastBusTiming -> true
                lastBusTiming.isAfter(beforeFirstBusTiming) -> {
                    // Same-day interval
                    !time.isBefore(beforeFirstBusTiming) && !time.isAfter(lastBusTiming)
                }
                else -> {
                    // Interval spans past midnight
                    !time.isBefore(beforeFirstBusTiming) || !time.isAfter(lastBusTiming)
                }
            }
        }
    }
}

/**
 * Convert a String to a LocalTime
 *
 * @param s the String input of pattern HHmm
 * @return the LocalTime representation of the String
 */
fun getTime(s: String): LocalTime? {
    try {
        val timeFormat = DateTimeFormatter.ofPattern("HHmm")
        return LocalTime.parse(s, timeFormat)
    } catch (e: DateTimeParseException) {
        return null
    }
}

/**
 * Check if a route is looped.
 *
 * @param route The FULL route list of a bus service
 * @return True if the route is looped, false otherwise
 */
fun isLoop(route: List<BusRouteInfoWithBusStopInfo>): Boolean {
    return route.first().busStopInfo.description == route.last().busStopInfo.description ||
            oppositeBusStopCode(route.first().busStopInfo.busStopCode) == route.last().busStopInfo.busStopCode
}

/**
 * Truncate the route until the nth bus stop.
 *
 * @param route The FULL route list of a bus service
 * @param stopSequence n
 * @param adjustStopSequence Whether the route returned have the truncated route stop sequence
 * and distance or the original route stop sequence and distance
 * @return The truncated route list of a bus service
 */
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

    if (visitedFirstStopAgain >= 0) { //bus route is a dual loop continuous
        truncatedRoute = truncatedRoute + addStopSequenceOffset(route, truncatedRoute.last())
                //if (adjustStopSequence) addStopSequenceOffset(route, truncatedRoute.last()) else route
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

/**
 * Add the stop sequence and distance of a stop to every stop in the route list.
 *
 * @param route The route list of a bus service.
 * @param firstStop The BusRouteInfo
 * @return The route list with the added stop sequence and distance
 */
private fun addStopSequenceOffset(
    route: List<BusRouteInfoWithBusStopInfo>,
    firstStop: BusRouteInfoWithBusStopInfo
): List<BusRouteInfoWithBusStopInfo> {
    return route.map { stop ->
        stop.copy(
            busRouteInfo =
                stop.busRouteInfo.copy(
                    stopSequence = stop.busRouteInfo.stopSequence + firstStop.busRouteInfo.stopSequence,
                    distance = round((stop.busRouteInfo.distance + firstStop.busRouteInfo.distance) * 10.0) / 10.0
                )
        )
    }
}

/**
 * Subtract the stop sequence and distance of every stop in the route list
 * from the first stop in the route list
 *
 * @param truncatedRoute The route list of a bus service
 * @return The route list with the updated stop sequence and distance
 */
private fun removeStopSequenceOffset(truncatedRoute: List<BusRouteInfoWithBusStopInfo>):
        List<BusRouteInfoWithBusStopInfo> {
    if (truncatedRoute.isNotEmpty()) {
        val distanceOffset = truncatedRoute.first().busRouteInfo.distance
        val sequenceOffset = truncatedRoute.first().busRouteInfo.stopSequence
        return truncatedRoute
            .map { stop ->
                val adjustedSequence = stop.busRouteInfo.stopSequence - sequenceOffset
                val adjustedDistance = round((stop.busRouteInfo.distance - distanceOffset) * 10.0) / 10.0
                stop.copy(busRouteInfo = stop.busRouteInfo.copy(
                    stopSequence = adjustedSequence,
                    distance = adjustedDistance)
                )
            }
    } else {
        return truncatedRoute
    }
}

/**
 * Drops every stop in the route list that is before the first stop of the looping point
 * or after the last stop of the looping point
 *
 * @param route The route list of a bus service
 * @param after Whether to drop before the first stop of the looping point
 * or after the last stop of the looping point
 * @return the stop sequence of the first stop in the truncated route list,
 * and the truncated route list itself
 */
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
        val stopSequenceOffset = (seenBusStopCodes[lastSeen] ?: 0) + 1
        return Pair(
            first = stopSequenceOffset,
            second = truncateTillBusStop(
                route = route,
                stopSequence = stopSequenceOffset
            )
        )
    }
}

/**
 * Get the bus stop code of the opposite bus stop, if it exists
 *
 * @param busStopCode The bus stop code of this bus stop
 * @return The bus stop code of the opposite bus stop, or the original bus stop code if it does not exist
 */
fun oppositeBusStopCode(busStopCode: String): String {
    return if (busStopCode.last() == '9') {
        busStopCode.substring(0, busStopCode.length - 1) + '1'
    } else if (busStopCode.last() == '1') {
        busStopCode.substring(0, busStopCode.length - 1) + '9'
    } else {
        busStopCode
    }
}

/**
 * Finds a list of bus services between two points
 *
 * @param origin The origin point
 * @param target The destination point
 * @param distanceThreshold The maximum distance between the origin point and the origin bus stop
 * added with that of between the destination bus stop and destination point
 * @param busRepository The repository of the bus data
 * @return A list of pairs of origin and destination bus stops, as well as the distance between
 * the origin or destination bus stops and the origin and destination point respectively
 */
suspend fun findBusServiceTo(
    origin: HasCoordinates,
    target: HasCoordinates,
    distanceThreshold: Int,
    busRepository: BusRepository
): List<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> {

    val distanceToTarget = target.distanceFromInMetres(origin)
    val busRoutesNearby: List<Pair<Int, BusRouteInfoWithBusStopInfo>> = findNearbyBusStops(
        point = origin,
        distanceThreshold = distanceThreshold,
        busRepository = busRepository
    )
        .flatMap { distanceAndBusStop ->
            busRepository
                .getBusRoutesAtBusStop(distanceAndBusStop.second.busStopCode)
                .map { busRoute ->
                    Pair(
                        first = distanceAndBusStop.first,
                        second = BusRouteInfoWithBusStopInfo(
                            busRouteInfo = busRoute,
                            busStopInfo = distanceAndBusStop.second
                        )
                    )
                }
        }

    val seenBusRoutes: HashSet<BusRouteInfoWithBusStopInfo> = HashSet()
    val routes: MutableList<Pair<Pair<Int, BusRouteInfoWithBusStopInfo>, Pair<Int, BusRouteInfoWithBusStopInfo>>> =
        mutableListOf()

    for (busRouteNearby in busRoutesNearby) {
        if (!seenBusRoutes.contains(busRouteNearby.second)) {
            val remainingDistanceThreshold = distanceThreshold - busRouteNearby.first
            val busRouteInfo = busRouteNearby.second.busRouteInfo
            val fullBusRoute =
                busRepository.getBusServiceRoute(
                    serviceNo = busRouteInfo.serviceNo,
                    direction = busRouteInfo.direction
                )
            val routeFromThisStop =
                truncateTillBusStop(
                    route = fullBusRoute,
                    stopSequence = busRouteInfo.stopSequence,
                    adjustStopSequence = false
                )
            var destination: Pair<Int, BusRouteInfoWithBusStopInfo>? = null
            var alternativeDestination: Pair<Int, BusRouteInfoWithBusStopInfo>? = null
            var alternativeOrigin: Pair<Int, BusRouteInfoWithBusStopInfo>? = null
            var readyToSetAlternativeOrigin = false
            var previousDistanceToOrigin: Int = busRouteNearby.first
            var destinationFixed = false

            for (stop in routeFromThisStop) {
                if (!seenBusRoutes.contains(stop)) {
                    seenBusRoutes.add(stop)
                    val distance = stop.busStopInfo.distanceFromInMetres(target)
                    val distanceToOrigin = origin.distanceFromInMetres(stop.busStopInfo)

                    if (distanceToOrigin < previousDistanceToOrigin) {
                        readyToSetAlternativeOrigin = true
                    }
                    previousDistanceToOrigin = distanceToOrigin

                    if (readyToSetAlternativeOrigin &&
                        distanceToOrigin < distanceThreshold &&
                        (alternativeOrigin == null
                                || alternativeOrigin.first > distanceToOrigin)
                    ) {
                        alternativeOrigin = Pair(distanceToOrigin, stop)
                    }

                    if (distance <= remainingDistanceThreshold) {

                        if (!destinationFixed) {
                            if (destination == null || destination.first > distance) {
                                destination = Pair(distance, stop)
                            } else {
                                destinationFixed = true
                            }
                        } else {
                            if (distance < destination!!.first &&
                                (alternativeDestination == null
                                        || alternativeDestination.first > distance)
                            ) {
                                alternativeDestination = Pair(distance, stop)
                            } else if (alternativeDestination != null) {
                                break
                            }
                        }

                    } else if (destination != null) {
                        destinationFixed = true
                        continue
                    }
                } else {
                    break
                }
            }
            if (checkProposedRoute(origin = busRouteNearby,
                    destination = destination,
                    distanceThreshold = distanceThreshold)) {
                routes.add(Pair(busRouteNearby, destination!!))
                if (checkProposedRoute(
                        origin = alternativeOrigin,
                        destination = destination,
                        distanceThreshold = distanceThreshold)) {
                    routes.add(Pair(alternativeOrigin!!, destination))
                }

                if (checkProposedRoute(
                        origin = busRouteNearby,
                        destination = alternativeDestination,
                        distanceThreshold = distanceThreshold)) {
                    routes.add(Pair(busRouteNearby, alternativeDestination!!))
                    if (checkProposedRoute(
                            origin = alternativeOrigin,
                            destination = alternativeDestination,
                            distanceThreshold = distanceThreshold)) {
                        routes.add(Pair(alternativeOrigin!!, alternativeDestination))
                    }
                }
            }
        }
    }
    return routes
}

private fun checkProposedRoute(
    origin: Pair<Int, BusRouteInfoWithBusStopInfo>?,
    destination: Pair<Int, BusRouteInfoWithBusStopInfo>?,
    distanceThreshold: Int
): Boolean {
    return origin != null &&
            destination != null &&
            origin.first + destination.first <= distanceThreshold &&
            destination.second.busRouteInfo.stopSequence - origin.second.busRouteInfo.stopSequence > 0
}

/**
 * Adds distance information between a point and all bus stops along a route
 *
 * @param point The point
 * @param route The route of a bus service
 * @return A list containing distance information between a point and a bus stop, and the bus stop itself.
 */
fun attachDistanceFromPoint(point: HasCoordinates, route: List<BusRouteInfoWithBusStopInfo>)
: List<Pair<Int, BusRouteInfoWithBusStopInfo>> {
    return route.map { Pair(it.busStopInfo.distanceFromInMetres(point), it) }
}


