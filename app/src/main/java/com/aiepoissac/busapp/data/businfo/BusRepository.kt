package com.aiepoissac.busapp.data.businfo

import com.aiepoissac.busapp.data.mrtstation.MRTStation

/**
 * This interface acts as a way to interact with the local Bus Database.
 */
interface BusRepository {

    suspend fun insertBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteBusService(busServiceInfo: BusServiceInfo)

    suspend fun updateBusService(busServiceInfo: BusServiceInfo)

    /**
     * Delete every bus service from the database
     */
    suspend fun deleteAllBusServices()

    /**
     * Get the number of bus services in the database
     *
     * @return The number of bus services in the database
     */
    suspend fun getBusServicesCount(): Int

    /**
     * Get all bus services and variants with this service number, including the
     * bus stop information of the origin and destination bus stops.
     *
     * @param serviceNo The bus service number
     * @return A list of bus services and variants with this service number
     */
    suspend fun getBusService(serviceNo: String): List<BusServiceInfoWithBusStopInfo>

    /**
     * Get the bus service with this exact service number and direction.
     *
     * @param serviceNo The bus service number
     * @param direction The direction in which the bus travels (1 or 2)
     * @return The BusServiceInfo of the bus service, or null if it does not exist
     */
    suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo?

    suspend fun insertBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun deleteBusRoute(busRouteInfo: BusRouteInfo)

    suspend fun updateBusRoute(busRouteInfo: BusRouteInfo)

    /**
     * Delete every bus route from the database
     */
    suspend fun deleteAllBusRoutes()

    /**
     * Get the number of bus route information in the database
     *
     * @return The number of bus route information in the database
     */
    suspend fun getBusRoutesCount(): Int

    /**
     * Get the full route of a bus service and direction from the database,
     * including information of the bus stops along the route.
     *
     * @param serviceNo The bus service number
     * @param direction The direction in which the bus travels (1 or 2)
     * @return A list that represents the full route of a bus service
     */
    suspend fun getBusServiceRoute(
        serviceNo: String, direction: Int
    ): List<BusRouteInfoWithBusStopInfo>

    /**
     * Get the length of the full route of a bus service and direction from the database
     *
     * @param serviceNo The bus service number
     * @param direction The direction in which the bus travels (1 or 2)
     * @return The length of the route
     */
    suspend fun getBusServiceRouteLength(serviceNo: String, direction: Int): Int

    /**
     * Get the route of a bus service after a specified stop,
     * including information of the bus stops along the route.
     *
     * @param serviceNo The bus service number
     * @param direction The direction in which the bus travels (1 or 2)
     * @param stopSequence The stop sequence of the specified stop along the route
     * @return A list that represents the route of a bus service after the specified stop
     */
    suspend fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String, direction: Int, stopSequence: Int
    ): List<BusRouteInfoWithBusStopInfo>

    /**
     * Get the route information of a bus service at a specified stop,
     * including information of the bus stop.
     *
     * @param serviceNo The bus service number
     * @param direction The direction in which the bus travels (1 or 2)
     * @param stopSequence The stop sequence of the specified stop along the route
     * @return The BusRouteInfo and BusStopInfo of the specified stop and bus service
     */
    suspend fun getBusRouteInfoWithBusStopInfo(
        serviceNo: String, direction: Int, stopSequence: Int
    ): BusRouteInfoWithBusStopInfo

    /**
     * Get bus route information of all bus services at a specified bus stop.
     *
     * @param busStopCode The unique 5-digit identifier for this physical bus stop
     * @return A list containing the bus route information
     */
    suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo>

    suspend fun insertBusStop(busStopInfo: BusStopInfo)

    suspend fun updateBusStop(busStopInfo: BusStopInfo)

    suspend fun deleteBusStop(busStopInfo: BusStopInfo)

    /**
     * Delete every bus stop from the database
     */
    suspend fun deleteAllBusStops()

    /**
     * Get the number of bus stops in the database
     *
     * @return The number of bus stops in the database
     */
    suspend fun getBusStopsCount(): Int

    /**
     * Get the bus stop information of a bus stop
     *
     * @param busStopCode The unique 5-digit identifier for this physical bus stop
     * @return The BusStopInfo of this bus stop, or null if it does not exist
     */
    suspend fun getBusStop(busStopCode: String): BusStopInfo?

    /**
     * Get all bus stops in the database
     *
     * @return A list of all bus stops in the database
     */
    suspend fun getAllBusStops(): List<BusStopInfo>

    /**
     * Get all bus stops that has a description that contains this substring
     *
     * @param partialDescription The substring
     * @return A list of all bus stops that has a description that contains this substring
     */
    suspend fun getBusStopsContaining(partialDescription: String): List<BusStopInfo>

    /**
     * Get all bus stops that has a bus stop code that starts with this substring
     *
     * @param busStopCode The unique 5-digit identifier for this physical bus stop
     * @return A list of all bus stops that has a bus stop code that starts with this substring
     */
    suspend fun getBusStopsWithPrefixCode(busStopCode: String): List<BusStopInfo>

    /**
     * Get all bus stops along roads which has a name containing this substring
     *
     * @param roadName The substring
     * @return A list of all bus stops along roads which has a name containing this substring
     */
    suspend fun getBusStopsWithPartialRoadName(roadName:String): List<BusStopInfo>

    /**
     * Get bus route information of all bus services at a specified bus stop,
     * including information of the bus stop
     *
     * @param busStopCode The unique 5-digit identifier for this physical bus stop
     * @return The bus stop information and a list containing the bus route information
     */
    suspend fun getBusStopWithBusRoutes(busStopCode: String): BusStopInfoWithBusRoutesInfo

    suspend fun insertMRTStation(mrtStation: MRTStation)

    suspend fun updateMRTStation(mrtStation: MRTStation)

    suspend fun deleteMRTStation(mrtStation: MRTStation)

    /**
     * Deletes all MRT stations from the database
     */
    suspend fun deleteAllMRTStations()

    /**
     * Get the number of MRT Stations in the database
     *
     * @return The number of MRT Stations
     */
    suspend fun getMRTStationCount(): Int

    /**
     * Get a MRT station with that station code.
     *
     * @param stationCode The station code
     * @return The MRT station with that station code
     */
    suspend fun getMRTStation(stationCode: String): MRTStation

    /**
     * Get the MRT stations with names containing this substring.
     *
     * @param partialName The substring
     * @return A list of MRT stations with names containing this substring.
     */
    suspend fun getMRTStationsContaining(partialName: String): List<MRTStation>

    /**
     * Get all MRT stations in the database.
     *
     * @return A list containing all MRT stations.
     */
    suspend fun getAllMRTStations(): List<MRTStation>

    suspend fun insertPlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo)

    suspend fun updatePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo)

    suspend fun deletePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo)

    /**
     * Delete every planned bus route from the database
     */
    suspend fun deleteAllPlannedBusRoutes()

    /**
     * Get the number of planned bus route information in the database
     *
     * @return The number of planned bus route information
     */
    suspend fun getPlannedBusRoutesCount(): Int

    /**
     * Get all planned bus route information in the database
     *
     * @return A list of all planned bus route information
     */
    suspend fun getAllPlannedBusRoutes(): List<PlannedBusRouteInfoWithBusStopInfo>

}