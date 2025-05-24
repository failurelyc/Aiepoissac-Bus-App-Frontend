package com.aiepoissac.busapp.data.businfo

class OfflineBusRepository(
    private val busServiceInfoDAO: BusServiceInfoDAO,
    private val busRouteInfoDAO: BusRouteInfoDAO,
    private val busStopInfoDAO: BusStopInfoDAO): BusRepository {

    override suspend fun insertBusService(busServiceInfo: BusServiceInfo) {
        busServiceInfoDAO.insert(busServiceInfo = busServiceInfo)
    }

    override suspend fun deleteBusService(busServiceInfo: BusServiceInfo) {
        busServiceInfoDAO.delete(busServiceInfo = busServiceInfo)
    }

    override suspend fun updateBusService(busServiceInfo: BusServiceInfo) {
        busServiceInfoDAO.update(busServiceInfo = busServiceInfo)
    }

    override suspend fun deleteAllBusServices() {
        busServiceInfoDAO.deleteAll()
    }

    override suspend fun getBusServicesCount(): Int {
        return busServiceInfoDAO.getBusServicesCount()
    }

    override suspend fun getBusService(serviceNo: String): List<BusServiceInfo> {
        return busServiceInfoDAO.getBusService(serviceNo)
    }

    override suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo {
        return busServiceInfoDAO.getBusService(serviceNo, direction)
    }

    override suspend fun insertBusRoute(busRouteInfo: BusRouteInfo) {
        busRouteInfoDAO.insert(busRouteInfo)
    }

    override suspend fun deleteBusRoute(busRouteInfo: BusRouteInfo) {
        busRouteInfoDAO.delete(busRouteInfo)
    }

    override suspend fun updateBusRoute(busRouteInfo: BusRouteInfo) {
        busRouteInfoDAO.update(busRouteInfo)
    }

    override suspend fun deleteAllBusRoutes() {
        busRouteInfoDAO.deleteAll()
    }

    override suspend fun getBusServiceRoute(serviceNo: String, direction: Int):
            List<BusRouteInfoWithBusStopInfo> {
        return busRouteInfoDAO.getBusServiceRoute(serviceNo, direction)
    }

    override suspend fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String, direction: Int, stopSequence: Int): List<BusRouteInfoWithBusStopInfo> {
        return busRouteInfoDAO
            .getBusServiceRouteAfterSpecifiedStop(serviceNo, direction, stopSequence)
    }

    override suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo> {
        return busRouteInfoDAO.getBusRoutesAtBusStop(busStopCode)
    }

    override suspend fun insertBusStop(busStopInfo: BusStopInfo) {
        busStopInfoDAO.insert(busStopInfo)
    }

    override suspend fun updateBusStop(busStopInfo: BusStopInfo) {
        busStopInfoDAO.update(busStopInfo)
    }

    override suspend fun deleteBusStop(busStopInfo: BusStopInfo) {
        busStopInfoDAO.delete(busStopInfo)
    }

    override suspend fun deleteAllBusStops() {
        busStopInfoDAO.deleteAll()
    }

    override suspend fun getBusStop(busStopCode: String): BusStopInfo? {
        return busStopInfoDAO.getBusStop(busStopCode)
    }

    override suspend fun getAllBusStops(): List<BusStopInfo> {
        return busStopInfoDAO.getAllBusStops()
    }

}