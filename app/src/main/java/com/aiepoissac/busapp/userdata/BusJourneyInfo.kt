package com.aiepoissac.busapp.userdata

import androidx.room.Entity
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.busarrival.getBusArrival
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import java.io.IOException

@Entity(
    tableName = "Bus_Journey_Info_Table",
    primaryKeys = ["journeyID", "sequence"]
)
data class BusJourneyInfo (
    val journeyID: String,
    val sequence: Int,
    val serviceNo: String,
    val direction: Int,
    val originBusStopSequence: Int,
    val destinationBusStopSequence: Int
) {
    suspend fun attachBusArrivalsAndBusRouteWithBusStopInfo(
        busRepository: BusRepository
    ): Pair<BusJourneyInfo, Pair<
            Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>,
            Pair<BusRouteInfoWithBusStopInfo, List<BusService>?>>>
    {
        val origin = busRepository.getBusRouteInfoWithBusStopInfo(
            serviceNo = serviceNo,
            direction = direction,
            stopSequence = originBusStopSequence
        )
        val destination = busRepository.getBusRouteInfoWithBusStopInfo(
            serviceNo = serviceNo,
            direction = direction,
            stopSequence = destinationBusStopSequence
        )
        try {
            val originBusArrivals = getBusArrival(origin.busRouteInfo.busStopCode)
                .getBusArrivalsOfASingleService(serviceNo)
            val destinationBusArrivals = getBusArrival(destination.busRouteInfo.busStopCode)
                .getBusArrivalsOfASingleService(serviceNo)
            return Pair(
                first = this,
                second = Pair(
                    first = Pair(origin, originBusArrivals),
                    second = Pair(destination, destinationBusArrivals)
                )
            )
        } catch (e: IOException) {
            return Pair(
                first = this,
                second = Pair(
                    first = Pair(origin, null),
                    second = Pair(destination, null)
                )

            )
        }
    }
}