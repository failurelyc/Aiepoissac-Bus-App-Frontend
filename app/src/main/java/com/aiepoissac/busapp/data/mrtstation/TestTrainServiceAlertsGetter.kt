package com.aiepoissac.busapp.data.mrtstation

import java.time.LocalDateTime
import java.time.Month

class TestTrainServiceAlertsGetter: TrainServiceAlertsGetter {
    override suspend fun getFacilitiesMaintenance(): FacilitiesMaintenance {
        return FacilitiesMaintenance(
            metadata = "Fake",
            value = listOf()
        )
    }

    override suspend fun getTrainServiceAlerts(): TrainServiceAlerts {
        return TrainServiceAlerts(
            metadata = "Fake",
            value = TrainServiceAlertsInfo(
                status = 2,
                affectedSegments =
                    listOf(
                        AffectedSegment(
                            line = "NSL",
                            direction = "Jurong East",
                            stations = "NS17,NS16,NS15,NS14,NS13,NS12,NS11,NS10,NS9",
                            freePublicBusStationCodes = "NS17,NS16,NS15,NS14,NS13,NS12,NS11,NS10,NS9",
                            freeMRTShuttleStationCodes = "",
                            mrtShuttleDirection = ""
                        ),
                        AffectedSegment(
                            line = "NEL",
                            direction = "Both",
                            stations = "NE15,NE16,NE17,NE18",
                            freePublicBusStationCodes = "NE15,NE16,NE17,NE18",
                            freeMRTShuttleStationCodes = "",
                            mrtShuttleDirection = ""
                        )
                    ),
                message =
                    listOf(
                        TrainServiceAlertMessage(
                            content = "1756hrs: No train service between Bishan and Woodlands stations towards Jurong East station" +
                                    " due to a signal fault. Free bus rides are available at designated stops",
                            createdDate = LocalDateTime.of(2017, Month.DECEMBER, 11, 17, 56, 50)
                        ),
                        TrainServiceAlertMessage(
                            content = "1711hrs : NEL - Additional Travel Time of 40 minutes between Buangkok and Punggol Coast" +
                                    " in both directions due to a signal fault. Free bus rides are available at designated stops" +
                                    "in both directions.",
                            createdDate = LocalDateTime.of(2017, Month.DECEMBER, 11, 17, 11, 27)
                        ),
                        TrainServiceAlertMessage(
                            content = "1657hrs : NEL - Additional Travel Time of 20 minutes between Buangkok and Punggol Coast" +
                                    " in both directions due to a signal fault. ",
                            createdDate = LocalDateTime.of(2017, Month.DECEMBER, 11, 16, 57, 25)
                        )
                    )
            )
        )
    }


}