package com.aiepoissac.busapp.data.businfo

interface BusRepository {

    suspend fun insertBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteBusService(busServiceInfo: BusServiceInfo)

    suspend fun updateBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteAllBusServices()

    suspend fun getBusServicesCount(): Int

    suspend fun getBusService(serviceNo: String): List<BusServiceInfoWithBusStopInfo>

    suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo?

    suspend fun insertBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun deleteBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun updateBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun deleteAllBusRoutes()

    suspend fun getBusRoutesCount(): Int

    suspend fun getBusServiceRoute(
        serviceNo: String, direction: Int
    ): List<BusRouteInfoWithBusStopInfo>

    suspend fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String, direction: Int, stopSequence: Int
    ): List<BusRouteInfoWithBusStopInfo>

    suspend fun getBusRouteInfoWithBusStopInfo(
        serviceNo: String, direction: Int, stopSequence: Int
    ): BusRouteInfoWithBusStopInfo

    suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo>

    suspend fun insertBusStop(busStopInfo: BusStopInfo)

    suspend fun updateBusStop(busStopInfo: BusStopInfo)

    suspend fun deleteBusStop(busStopInfo: BusStopInfo)

    suspend fun deleteAllBusStops()

    suspend fun getBusStopsCount(): Int

    suspend fun getBusStop(busStopCode: String): BusStopInfo?

    suspend fun getAllBusStops(): List<BusStopInfo>

    suspend fun getBusStopsContaining(partialDescription: String): List<BusStopInfo>

    suspend fun getBusStopsWithPrefixCode(busStopCode: String): List<BusStopInfo>

    suspend fun getBusStopsWithPartialRoadName(roadName:String): List<BusStopInfo>

    suspend fun getBusStopWithBusRoutes(busStopCode: String): BusStopInfoWithBusRoutesInfo

    suspend fun insertMRTStation(mrtStation: MRTStation)

    suspend fun updateMRTStation(mrtStation: MRTStation)

    suspend fun deleteMRTStation(mrtStation: MRTStation)

    suspend fun deleteAllMRTStations()

    suspend fun getMRTStationCount(): Int

    suspend fun getMRTStation(stationCode: String): MRTStation

    suspend fun getMRTStationsContaining(partialName: String): List<MRTStation>

    suspend fun getAllMRTStations(): List<MRTStation>

    suspend fun insertPlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo)

    suspend fun updatePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo)

    suspend fun deletePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo)

    suspend fun deleteAllPlannedBusRoutes()

    suspend fun getAllPlannedBusRoutes(): List<PlannedBusRouteInfoWithBusStopInfo>

}