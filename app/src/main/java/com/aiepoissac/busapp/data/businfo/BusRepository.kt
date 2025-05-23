package com.aiepoissac.busapp.data.businfo

interface BusRepository {

    suspend fun insertBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteBusService(busServiceInfo: BusServiceInfo)

    suspend fun updateBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteAllBusServices()

    suspend fun getBusServicesCount(): Int

    suspend fun getBusService(serviceNo: String): List<BusServiceInfo>

    suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo

    suspend fun insertBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun deleteBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun updateBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun deleteAllBusRoutes()

    suspend fun getBusServiceRoute(serviceNo: String, direction: Int):
            List<BusRouteInfoWithBusStopInfo>

    suspend fun getBusServiceRouteAfterSpecifiedStop(serviceNo: String, direction: Int, stopSequence: Int):
            List<BusRouteInfoWithBusStopInfo>

    suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo>

    suspend fun insertBusStop(busStopInfo: BusStopInfo)

    suspend fun updateBusStop(busStopInfo: BusStopInfo)

    suspend fun deleteBusStop(busStopInfo: BusStopInfo)

    suspend fun deleteAllBusStops()

    suspend fun getBusStop(busStopCode: String): BusStopInfo?
}