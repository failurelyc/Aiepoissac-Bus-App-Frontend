package com.aiepoissac.busapp.data

import android.content.Context
import com.aiepoissac.busapp.LocationRepository
import com.aiepoissac.busapp.RealLocationRepository
import com.aiepoissac.busapp.data.bicycle.BicycleParkingGetter
import com.aiepoissac.busapp.data.bicycle.RealBicycleParkingGetter
import com.aiepoissac.busapp.data.busarrival.BusArrivalGetter
import com.aiepoissac.busapp.data.busarrival.RealBusArrivalGetter
import com.aiepoissac.busapp.data.businfo.BusDatabase
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.OfflineBusRepository
import com.aiepoissac.busapp.userdata.OfflineUserDataRepository
import com.aiepoissac.busapp.userdata.UserDataDatabase
import com.aiepoissac.busapp.userdata.UserDataRepository

interface AppContainer {
    val busRepository: BusRepository
    val userDataRepository: UserDataRepository
    val busArrivalGetter: BusArrivalGetter
    val bicycleParkingGetter: BicycleParkingGetter
    val locationRepository: LocationRepository
}

class AppDataContainer(private val context: Context): AppContainer {

    override val busRepository: BusRepository by lazy {
        OfflineBusRepository(
            busServiceInfoDAO = BusDatabase.getDatabase(context).busServiceInfoDao(),
            busRouteInfoDAO = BusDatabase.getDatabase(context).busRouteInfoDAO(),
            busStopInfoDAO = BusDatabase.getDatabase(context).busStopInfoDAO(),
            mrtStationDAO = BusDatabase.getDatabase(context).mrtStationDAO(),
            plannedBusRouteInfoDAO = BusDatabase.getDatabase(context).plannedBusRouteInfoDAO()
        )
    }

    override val userDataRepository: UserDataRepository by lazy {
        OfflineUserDataRepository(
            busJourneyInfoDAO = UserDataDatabase.getDatabase(context).busJourneyInfoDAO(),
            busJourneyListInfoDAO = UserDataDatabase.getDatabase(context).busJourneyListInfoDAO()
        )
    }

    override val busArrivalGetter: BusArrivalGetter by lazy { RealBusArrivalGetter() }

    override val bicycleParkingGetter: BicycleParkingGetter by lazy { RealBicycleParkingGetter() }

    override val locationRepository: LocationRepository by lazy { RealLocationRepository() }
}