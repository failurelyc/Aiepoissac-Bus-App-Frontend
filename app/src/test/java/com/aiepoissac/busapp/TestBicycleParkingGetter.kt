package com.aiepoissac.busapp

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.bicycle.BicycleParking
import com.aiepoissac.busapp.data.bicycle.BicycleParkingGetter
import java.io.IOException

class NoInternetBicycleParkingGetter: BicycleParkingGetter {

    override suspend fun getBicycleParking(point: HasCoordinates): List<BicycleParking> {
        throw IOException("No internet connection")
    }

}

class TestBicycleParkingGetter: BicycleParkingGetter {

    override suspend fun getBicycleParking(point: HasCoordinates): List<BicycleParking> {
        if (point.latitude == 1.2948117103061263 && point.longitude == 103.78437918054274
            || point.latitude == 1.2950030435540145 && point.longitude == 103.78460883908544) {
            return listOf(
                BicycleParking(
                    description = "KENT RIDGE MRT A",
                    latitude = 1.294169,
                    longitude = 103.78445,
                    rackType = "MRT_RACKS",
                    rackCount = 40,
                    shelterIndicator = "N"
                ),
                BicycleParking(
                    description = "KENT RIDGE MRT B",
                    latitude = 1.292575,
                    longitude = 103.78504,
                    rackType = "MRT_RACKS",
                    rackCount = 40,
                    shelterIndicator = "N"
                ),
                BicycleParking(
                    description = "BUS STOP 15139",
                    latitude = 1.292593,
                    longitude = 103.784866,
                    rackType = "Yellow Box",
                    rackCount = 13,
                    shelterIndicator = "N"
                ),
                BicycleParking(
                    description = "BUS STOP 18339",
                    latitude = 1.294861,
                    longitude = 103.784685,
                    rackType = "Yellow Box",
                    rackCount = 13,
                    shelterIndicator = "N"
                )
            )
        } else if (point.latitude == 1.2965671166736066 && point.longitude == 103.77254247576647
            || point.latitude == 1.2972176268958733 && point.longitude == 103.7726877061283) {
            return listOf(
                BicycleParking(
                    description = "BUS STOP 16139",
                    latitude = 1.295406,
                    longitude = 103.769811,
                    rackType = "Yellow Box",
                    rackCount = 13,
                    shelterIndicator = "N"
                ),
                BicycleParking(
                    description = "BUS STOP 16141",
                    latitude = 1.297665,
                    longitude = 103.76957,
                    rackType = "Yellow Box",
                    rackCount = 10,
                    shelterIndicator = "N"
                ),
                BicycleParking(
                    description = "BUS STOP 16149",
                    latitude = 1.297525,
                    longitude = 103.769871,
                    rackType = "Yellow Box",
                    rackCount = 13,
                    shelterIndicator = "N"
                )
            )
        } else if (point.latitude == 1.2989822232795727 && point.longitude == 103.77417544854725
            || point.latitude == 1.2989025862071986 && point.longitude == 103.77438028222298) {
            return listOf(
                BicycleParking(
                    description = "BUS STOP 16099",
                    latitude = 1.300542,
                    longitude = 103.778288,
                    rackType = "Yellow Box",
                    rackCount = 10,
                    shelterIndicator = "N"
                )
            )
        } else return listOf()
    }

}