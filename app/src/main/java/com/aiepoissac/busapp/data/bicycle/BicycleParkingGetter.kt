package com.aiepoissac.busapp.data.bicycle

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.getData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

/**
 * This interface provides bicycle parking data.
 */
interface BicycleParkingGetter {

    /**
     * Gets all bicycle parking locations near the location provided.
     *
     * @param point The location
     * @return A list of bicycle parking locations
     */
    suspend fun getBicycleParking(point: HasCoordinates) : List<BicycleParking>

}

/**
 * This class provides bicycle parking data from the LTA API.
 */
class RealBicycleParkingGetter : BicycleParkingGetter {

    override suspend fun getBicycleParking(point: HasCoordinates): List<BicycleParking> {
        val json = withContext(Dispatchers.IO) {
            getBicycleParkingData(point.latitude, point.longitude)
        }
        return Json.decodeFromString<BicycleParkingList>(json).value
    }

    private suspend fun getBicycleParkingData(
        latitude: Double,
        longitude: Double
    ): String {
        val url = URL("https://datamall2.mytransport.sg/ltaodataservice/BicycleParkingv2?Lat=$latitude&Long=$longitude")
        return getData(url)
    }

}