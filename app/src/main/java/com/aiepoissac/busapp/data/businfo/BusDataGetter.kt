package com.aiepoissac.busapp.data.businfo

import com.aiepoissac.busapp.APIKeyManager
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

private enum class BusDataType {
    BusServices,
    BusRoutes,
    BusStops,
    PlannedBusRoutes
}

/**
 * This function calls the LTA API using the URL provided.
 *
 * @param url The URL
 * @return The JSON String returned by the LTA API
 */
suspend fun getData(url: URL): String = withContext(Dispatchers.IO) {
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

/**
 * This function calls the LTA API using the data type and offset provided.
 * The offset is required as the API only returns a maximum of 500 records per call
 * for these data types
 *
 * @param dataType Either BusServices, BusRoutes, BusStops or PlannedBusRoutes
 * @param offset The index of the first data to be returned.
 * @return The JSON String returned by the LTA API
 */
private suspend fun getData(dataType: BusDataType, offset: Int = 0): String {
    val url: URL = when (dataType) {
        BusDataType.BusStops -> {
            URL("https://datamall2.mytransport.sg/ltaodataservice/BusStops?\$skip=$offset")
        }
        BusDataType.BusRoutes -> {
            URL("https://datamall2.mytransport.sg/ltaodataservice/BusRoutes?\$skip=$offset")
        }
        BusDataType.BusServices -> {
            URL("https://datamall2.mytransport.sg/ltaodataservice/BusServices?\$skip=$offset")
        }
        BusDataType.PlannedBusRoutes -> {
            URL("https://datamall2.mytransport.sg/ltaodataservice/PlannedBusRoutes")
        }
    }
    return getData(url)
}

/**
 * This function repopulates all bus services using data from the LTA API.
 *
 * @param busRepository The repository of the bus data
 */
suspend fun populateBusServices(busRepository: BusRepository) {
    busRepository.deleteAllBusServices()
    var i = 0
    while (true) {
        val json = withContext(Dispatchers.IO) {
            getData(dataType = BusDataType.BusServices, offset = i * 500)
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

/**
 * This function repopulates all bus routes using data from the LTA API.
 *
 * @param busRepository The repository of the bus data
 */
suspend fun populateBusRoutes(busRepository: BusRepository) {
    busRepository.deleteAllBusRoutes()
    var i = 0
    var previous: BusRouteInfo? = null
    while (true) {
        val json = withContext(Dispatchers.IO) {
            getData(dataType = BusDataType.BusRoutes, offset = i * 500)
        }
        val busRoutesInfo: BusRoutesInfo = Json.decodeFromString<BusRoutesInfo>(json)
        if (busRoutesInfo.value.isEmpty()) {
            break
        } else {
            for (busRouteInfo in busRoutesInfo.value) {
                val busRouteInfoWithCorrectStopSequence = busRouteInfo.copy(
                    stopSequence = if (previous != null &&
                        previous.serviceNo == busRouteInfo.serviceNo &&
                        previous.direction == busRouteInfo.direction
                    ) previous.stopSequence + 1 else 0
                )
                busRepository.insertBusRoute(
                    busRouteInfoWithCorrectStopSequence
                )
                previous = busRouteInfoWithCorrectStopSequence

            }
        }
        i++
    }
}

/**
 * This function repopulates all bus stops using data from the LTA API.
 *
 * @param busRepository The repository of the bus data
 */
suspend fun populateBusStops(busRepository: BusRepository) {
    busRepository.deleteAllBusStops()
    var i = 0
    while (true) {
        val json = withContext(Dispatchers.IO) {
            getData(dataType = BusDataType.BusStops, offset = i * 500)
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

suspend fun populateMRTStations(busRepository: BusRepository) = withContext(Dispatchers.IO) {
    busRepository.deleteAllMRTStations()
    val inputStream = BusApplication.instance.assets.open("MRT Stations.csv")
    val scanner = Scanner(inputStream)
    scanner.useDelimiter("[,\\n]")
    while (scanner.hasNext()) {
        val type = scanner.next()
        val stationCode = scanner.next()
        val stationName = scanner.next()
        val latitude = scanner.next().toDouble()
        val longitude = scanner.next().toDouble()
        val mrtStation = MRTStation(type, stationCode, stationName, latitude, longitude)
        busRepository.insertMRTStation(mrtStation)
    }
    scanner.close()
    inputStream.close()
}

/**
 * This function repopulates all planned bus routes using data from the LTA API.
 *
 * @param busRepository The repository of the bus data
 */
suspend fun populatePlannedBusRoutes(busRepository: BusRepository) {
    busRepository.deleteAllPlannedBusRoutes()
    var previous: PlannedBusRouteInfo? = null
    val json = withContext(Dispatchers.IO) {
        getData(dataType = BusDataType.PlannedBusRoutes)
    }
    val plannedBusRoutesInfo: PlannedBusRoutesInfo = Json.decodeFromString<PlannedBusRoutesInfo>(json)

    for (plannedBusRouteInfo in plannedBusRoutesInfo.value) {
        val plannedBusRouteInfoWithCorrectStopSequence = plannedBusRouteInfo.copy(
            stopSequence = if (previous != null &&
                previous.serviceNo == plannedBusRouteInfo.serviceNo &&
                previous.direction == plannedBusRouteInfo.direction
            ) previous.stopSequence + 1 else 0
        )
        busRepository.insertPlannedBusRoute(
            plannedBusRouteInfoWithCorrectStopSequence
        )
        previous = plannedBusRouteInfoWithCorrectStopSequence
    }
}