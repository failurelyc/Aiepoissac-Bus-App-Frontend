package com.aiepoissac.busapp

import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.BusArrivalGetter
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.busarrival.BusStop
import java.io.IOException
import java.time.LocalDateTime
import java.time.Month

class NoInternetBusArrivalGetter : BusArrivalGetter {
    override suspend fun getBusArrival(busStopCode: String): BusStop {
        throw IOException("No internet connection")
    }
}

class TestBusArrivalGetter : BusArrivalGetter {
    override suspend fun getBusArrival(busStopCode: String): BusStop {
        if (busStopCode == "1000169") {
            return BusStop(
                busStopCode = busStopCode,
                services = listOf(
                    BusService(
                        serviceNo = "1004",
                        operator = "",
                        nextBus = Bus(
                            originCode = "1000169",
                            destinationCode = "1000169",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 30, 0),
                            monitored = 1,
                            latitude = 1.2949181152540041,
                            longitude = 103.77493256351714,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                        nextBus2 = Bus(
                            originCode = "1000169",
                            destinationCode = "1000169",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 32, 0),
                            monitored = 1,
                            latitude = 1.2909442886549687,
                            longitude = 103.78110146793922,
                            visitNumber = "2",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                        nextBus3 = Bus(
                            originCode = "1000169",
                            destinationCode = "1000169",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 35, 0),
                            monitored = 0,
                            latitude = 0.0,
                            longitude = 0.0,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                    )
                )
            )
        } else if (busStopCode == "18309") {
            return BusStop(
                busStopCode = busStopCode,
                services = listOf(
                    BusService(
                        serviceNo = "1004",
                        operator = "",
                        nextBus = Bus(
                            originCode = "1000169",
                            destinationCode = "1000169",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 33, 0),
                            monitored = 1,
                            latitude = 1.3035640377827036,
                            longitude = 103.77441333021167,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                        nextBus2 = Bus(
                            originCode = "1000169",
                            destinationCode = "1000169",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 38, 0),
                            monitored = 1,
                            latitude = 1.2971385871545937,
                            longitude = 103.7787816322459,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                        nextBus3 = Bus(
                            originCode = "1000169",
                            destinationCode = "1000169",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 43, 0),
                            monitored = 1,
                            latitude = 1.2918202990643677,
                            longitude = 103.78042627545256,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        )
                    ),
                    BusService(
                        serviceNo = "1011",
                        operator = "",
                        nextBus = Bus(
                            originCode = "1000151",
                            destinationCode = "1000159",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 35, 0),
                            monitored = 1,
                            latitude = 1.3007452197593086,
                            longitude = 103.769926979676,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                        nextBus2 = Bus(
                            originCode = "1000151",
                            destinationCode = "1000159",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 40, 0),
                            monitored = 1,
                            latitude = 1.2989025862071986,
                            longitude = 103.77438028222298,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        ),
                        nextBus3 = Bus(
                            originCode = "1000151",
                            destinationCode = "1000159",
                            estimatedArrival = LocalDateTime
                                .of(2025, Month.AUGUST, 11, 9, 50, 0),
                            monitored = 0,
                            latitude = 0.0,
                            longitude = 0.0,
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "SD"
                        )
                    )
                )
            )
        } else {
            return BusStop(busStopCode = busStopCode)
        }
    }



}