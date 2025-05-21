package com.aiepoissac.busapp.data.businfo

import kotlinx.coroutines.flow.Flow

class OfflineBusRepository(
    private val busServiceInfoDAO: BusServiceInfoDAO,
    private val busRouteInfoDAO: BusRouteInfoDAO): BusRepository {

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

    override fun getBusServicesCount(): Int {
        return busServiceInfoDAO.getBusServicesCount()
    }

    override fun getBusService(serviceNo: String): Flow<List<BusServiceInfo>> {
        return busServiceInfoDAO.getBusService(serviceNo)
    }

    override fun getBusService(serviceNo: String, direction: Int): Flow<BusServiceInfo> {
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

    override fun getBusServiceRoute(serviceNo: String, direction: Int): Flow<List<BusRouteInfo>> {
        return busRouteInfoDAO.getBusServiceRoute(serviceNo, direction)
    }

    override fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String, direction: Int, stopSequence: Int): Flow<List<BusRouteInfo>> {
        return busRouteInfoDAO.getBusServiceRouteAfterSpecifiedStop(serviceNo, direction, stopSequence)
    }

    override fun getBusRoutesAtBusStop(busStopCode: String): Flow<List<BusRouteInfo>> {
        return busRouteInfoDAO.getBusRoutesAtBusStop(busStopCode)
    }
}