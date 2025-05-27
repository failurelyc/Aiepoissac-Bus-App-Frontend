package com.aiepoissac.busapp.data

import android.content.Context
import com.aiepoissac.busapp.data.businfo.BusDatabase
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.OfflineBusRepository

interface AppContainer {
    val busRepository: BusRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val busRepository: BusRepository by lazy {
        OfflineBusRepository(
            busServiceInfoDAO = BusDatabase.getDatabase(context).busServiceInfoDao(),
            busRouteInfoDAO = BusDatabase.getDatabase(context).busRouteInfoDAO(),
            busStopInfoDAO = BusDatabase.getDatabase(context).busStopInfoDAO(),
            mrtStationDAO = BusDatabase.getDatabase(context).mrtStationDAO()
        )
    }
}