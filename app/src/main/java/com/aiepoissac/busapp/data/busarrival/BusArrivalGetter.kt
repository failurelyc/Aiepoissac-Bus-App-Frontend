package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.businfo.getData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

/**
 * This interface provides bus arrival data.
 */
interface BusArrivalGetter {

    suspend fun getBusArrival(busStopCode: String): BusStop

}

/**
 * This class provides bus arrival data from the LTA API.
 */
class RealBusArrivalGetter : BusArrivalGetter {

    override suspend fun getBusArrival(busStopCode: String): BusStop {
        val json = withContext(Dispatchers.IO) {
            getBusArrivalData(busStopCode)
        }
        return Json.decodeFromString<BusStop>(json)
    }

    private suspend fun getBusArrivalData(busStopCode: String): String {
        val url = URL("https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival?BusStopCode=$busStopCode")
        return getData(url)
    }

}