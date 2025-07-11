package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.APIKeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

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

    private suspend fun getBusArrivalData(busStopCode: String): String = withContext(Dispatchers.IO) {
        val url = URL("https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival?BusStopCode=$busStopCode")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("AccountKey", APIKeyManager.LTA)
        connection.setRequestProperty("accept", "application/json")
        connection.connect()

        if (connection.responseCode != 200) {
            throw IOException(connection.responseMessage)
        } else {
            val result = StringBuilder()
            val scanner = Scanner(connection.inputStream)
            while (scanner.hasNext()) {
                result.append(scanner.nextLine())
            }
            scanner.close()
            return@withContext result.toString()
        }
    }

}