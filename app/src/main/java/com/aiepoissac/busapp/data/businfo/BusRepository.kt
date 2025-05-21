package com.aiepoissac.busapp.data.businfo

import kotlinx.coroutines.flow.Flow

interface BusRepository {

    suspend fun insertBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteBusService(busServiceInfo: BusServiceInfo)

    suspend fun updateBusService(busServiceInfo: BusServiceInfo)

    suspend fun deleteAllBusServices()

    fun getBusServicesCount(): Int

    fun getBusService(serviceNo: String): Flow<List<BusServiceInfo>>

    fun getBusService(serviceNo: String, direction: Int): Flow<BusServiceInfo>

}