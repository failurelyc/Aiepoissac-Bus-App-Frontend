package com.aiepoissac.busapp.userdata

import androidx.room.Entity
import com.aiepoissac.busapp.data.busarrival.BusArrivalGetter
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import java.io.IOException

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

    fun isSameJourneyAs(other: JourneySegmentInfo): Boolean {
        return serviceNo == other.serviceNo
                && direction == other.direction
                && originBusStopSequence == other.originBusStopSequence
                && destinationBusStopSequence == other.destinationBusStopSequence
    }

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