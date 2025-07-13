package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.businfo.getData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

interface BusArrivalGetter {

    suspend fun getBusArrival(busStopCode: String): BusStop

}

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