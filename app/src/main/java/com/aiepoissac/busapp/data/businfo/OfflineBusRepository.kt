package com.aiepoissac.busapp.data.businfo

import kotlinx.coroutines.flow.Flow

class OfflineBusRepository(private val busServiceInfoDAO: BusServiceInfoDAO): BusRepository {

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

}