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

private suspend fun getData(dataType: BusDataType, count: Int): String = withContext(Dispatchers.IO) {
    val url: URL?
    if (dataType == BusDataType.BusStops) {
        url = URL("https://datamall2.mytransport.sg/ltaodataservice/BusStops?\$skip=$count")
    } else if (dataType == BusDataType.BusRoutes) {
        url = URL("https://datamall2.mytransport.sg/ltaodataservice/BusRoutes?\$skip=$count")
    } else if (dataType == BusDataType.BusServices) {
        url = URL("https://datamall2.mytransport.sg/ltaodataservice/BusServices?\$skip=$count")
    } else throw IllegalArgumentException()
    val connection = url.openConnection() as HttpURLConnection
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

suspend fun populateBusRoutes(busRepository: BusRepository) {
    busRepository.deleteAllBusRoutes()
    var i = 0
    while (true) {
        val json = withContext(Dispatchers.IO) {
            getData(dataType = BusDataType.BusRoutes, count = i * 500)
        }
        val busRoutesInfo: BusRoutesInfo = Json.decodeFromString<BusRoutesInfo>(json)
        if (busRoutesInfo.value.isEmpty()) {
            break
        } else {
            for (busRouteInfo in busRoutesInfo.value) {
                busRepository.insertBusRoute(busRouteInfo)
            }
        }
        i++
    }
}

suspend fun populateBusStops(busRepository: BusRepository) {
    busRepository.deleteAllBusStops()
    var i = 0
    while (true) {
        val json = withContext(Dispatchers.IO) {
            getData(dataType = BusDataType.BusStops, count = i * 500)
        }
        val busStopsInfo: BusStopsInfo = Json.decodeFromString(json)
        if (busStopsInfo.value.isEmpty()) {
            break
        } else {
            for (busStopInfo in busStopsInfo.value) {
                busRepository.insertBusStop(busStopInfo)
            }
        }
        i++
    }
}