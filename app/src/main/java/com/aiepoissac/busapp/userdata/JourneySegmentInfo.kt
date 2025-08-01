package com.aiepoissac.busapp.userdata

import androidx.room.Entity
import com.aiepoissac.busapp.data.busarrival.BusArrivalGetter
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import java.io.IOException

/**
 * This class contains information of a segment of the Bus Journey.
 *
 * @param journeyID The ID of the journey this segment belongs to
 * @param sequence The sequence of this segment within the journey
 * @param serviceNo The bus service number
 * @param direction The direction in which the bus travels (1 or 2)
 * @param originBusStopSequence The sequence of the origin bus stop along the bus route
 * @param destinationBusStopSequence The sequence of the destination bus stop along the bus route
 */
@Entity(
    tableName = "Bus_Journey_Info_Table",
    primaryKeys = ["journeyID", "sequence"]
)
data class JourneySegmentInfo (
    val journeyID: String,
    val sequence: Int,
    val serviceNo: String,
    val direction: Int,
    val originBusStopSequence: Int,
    val destinationBusStopSequence: Int
) {

    /**
     * Check if the segment is the same as another segment in terms of
     * only the origin, destination and bus service.
     *
     * @param other The other segment.
     * @return True if the other segment is the same as this segment, false otherwise
     */
    fun isSameJourneyAs(other: JourneySegmentInfo): Boolean {
        return serviceNo == other.serviceNo
                && direction == other.direction
                && originBusStopSequence == other.originBusStopSequence
                && destinationBusStopSequence == other.destinationBusStopSequence
    }

    /**
     * Add bus arrivals and bus routes information, including bus stop information,
     * of the origin and destination to this journey segment.
     *
     * @param busRepository The repository of the bus data
     * @param busArrivalGetter The bus arrival data source
     * @return A pair containing the journey segment and the other data
     */
    suspend fun attachBusArrivalsAndBusRouteWithBusStopInfo(
        busRepository: BusRepository,
        busArrivalGetter: BusArrivalGetter
    ): Pair<JourneySegmentInfo, Pair<
            Pair<BusRouteInfoWithBusStopInfo, BusService?>,
            Pair<BusRouteInfoWithBusStopInfo, BusService?>>> {
        return attachBusArrivalsToBusJourneyWithBusRouteInfo(
            busJourneyWithBusRouteInfo = attachBusRouteWithBusStopInfo(busRepository = busRepository),
            busRepository = busRepository,
            busArrivalGetter = busArrivalGetter
        )
    }

    /**
     * Add bus bus routes information, including bus stop information,
     * of the origin and destination to this journey segment.
     *
     * @param busRepository The repository of the bus data
     * @return A pair containing the journey segment and the other data
     */
    private suspend fun attachBusRouteWithBusStopInfo(
        busRepository: BusRepository
    ): Pair<JourneySegmentInfo, Pair<BusRouteInfoWithBusStopInfo, BusRouteInfoWithBusStopInfo>> {

        return Pair(
            first = this,
            second = Pair(
                first = busRepository.getBusRouteInfoWithBusStopInfo(
                    serviceNo = serviceNo,
                    direction = direction,
                    stopSequence = originBusStopSequence
                ),
                second = busRepository.getBusRouteInfoWithBusStopInfo(
                    serviceNo = serviceNo,
                    direction = direction,
                    stopSequence = destinationBusStopSequence
                )
            )
        )

    }
}

/**
 * Add bus arrivals information to this journey segment including bus route and bus stop information,
 * of the origin and destination.
 *
 * @param busJourneyWithBusRouteInfo The journey segment
 * including bus route and bus stop information of the origin and destination
 * @param busRepository The repository of the bus data
 * @param busArrivalGetter The bus arrival data source
 * @return A pair containing the journey segment and the other data
 */
suspend fun attachBusArrivalsToBusJourneyWithBusRouteInfo(
    busJourneyWithBusRouteInfo: Pair<JourneySegmentInfo, Pair<BusRouteInfoWithBusStopInfo, BusRouteInfoWithBusStopInfo>>,
    busRepository: BusRepository,
    busArrivalGetter: BusArrivalGetter
): Pair<JourneySegmentInfo, Pair<
        Pair<BusRouteInfoWithBusStopInfo, BusService?>,
        Pair<BusRouteInfoWithBusStopInfo, BusService?>>> {
    val origin = busJourneyWithBusRouteInfo.second.first
    val destination = busJourneyWithBusRouteInfo.second.second
    try {
        var originBusArrivals = busArrivalGetter.getBusArrival(origin.busRouteInfo.busStopCode)
            .getBusArrivalsOfASingleService(origin.busRouteInfo.serviceNo)

        if (originBusArrivals.size > 1) {
            val busServiceInfo = busRepository.getBusService(
                serviceNo = origin.busRouteInfo.serviceNo,
                direction = origin.busRouteInfo.direction
            )
            originBusArrivals = originBusArrivals.filter {
                it.nextBus.destinationCode == busServiceInfo?.destinationCode
            }
        }

        var destinationBusArrivals = busArrivalGetter.getBusArrival(destination.busRouteInfo.busStopCode)
            .getBusArrivalsOfASingleService(destination.busRouteInfo.serviceNo)

        if (destinationBusArrivals.size > 1) {
            val busServiceInfo = busRepository.getBusService(
                serviceNo = destination.busRouteInfo.serviceNo,
                direction = destination.busRouteInfo.direction
            )
            destinationBusArrivals = destinationBusArrivals.filter {
                it.nextBus.destinationCode == busServiceInfo?.destinationCode
            }
        }

        return Pair(
            first = busJourneyWithBusRouteInfo.first,
            second = Pair(
                first = Pair(origin, originBusArrivals.firstOrNull()),
                second = Pair(destination, destinationBusArrivals.firstOrNull())
            )
        )
    } catch (e: IOException) {
        return Pair(
            first = busJourneyWithBusRouteInfo.first,
            second = Pair(
                first = Pair(origin, null),
                second = Pair(destination, null)
            )

        )
    }
}