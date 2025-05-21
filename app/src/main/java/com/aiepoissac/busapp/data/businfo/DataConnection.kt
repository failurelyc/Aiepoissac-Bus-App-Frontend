package com.aiepoissac.busapp.data.businfo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner


enum class BusDataType {
    BusServices,
    BusRoutes,
    BusStops
}

suspend fun getData(dataType: BusDataType, count: Int): String = withContext(Dispatchers.IO) {
    var url: URL? = null
    if (dataType == BusDataType.BusStops) {
        url = URL("https://datamall2.mytransport.sg/ltaodataservice/BusStops?\$skip=$count")
    } else if (dataType == BusDataType.BusRoutes) {
        url = URL("https://datamall2.mytransport.sg/ltaodataservice/BusRoutes?\$skip=$count")
    } else if (dataType == BusDataType.BusServices) {
        url = URL("https://datamall2.mytransport.sg/ltaodataservice/BusServices?\$skip=$count")
    }
    val connection = url!!.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("AccountKey", "***REMOVED***")
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


suspend fun populateBusServices(busRepository: BusRepository) {
    busRepository.deleteAllBusServices()
    var i = 0
    while (true) {
        val json = withContext(Dispatchers.IO) {
            getData(dataType = BusDataType.BusServices, count = i * 500)
        }
        val busServicesInfo: BusServicesInfo = Json.decodeFromString<BusServicesInfo>(json)
        if (busServicesInfo.value.isEmpty()) {
            break
        } else {
            for (busServiceInfo in busServicesInfo.value) {
                busRepository.insertBusService(busServiceInfo)
            }
        }

        i++
    }
}