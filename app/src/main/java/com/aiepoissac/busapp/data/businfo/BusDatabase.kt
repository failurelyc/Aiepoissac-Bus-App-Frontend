package com.aiepoissac.busapp.data.businfo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aiepoissac.busapp.data.mrtstation.MRTStation
import com.aiepoissac.busapp.data.mrtstation.MRTStationDAO

@Database(
    entities = [
        BusServiceInfo::class, BusRouteInfo::class, BusStopInfo::class, MRTStation::class, PlannedBusRouteInfo::class],
    version = 2,
    exportSchema = false
)
abstract class BusDatabase: RoomDatabase() {

    abstract fun busServiceInfoDao(): BusServiceInfoDAO

    abstract fun busRouteInfoDAO(): BusRouteInfoDAO

    abstract fun busStopInfoDAO(): BusStopInfoDAO

    abstract fun mrtStationDAO(): MRTStationDAO

    abstract fun plannedBusRouteInfoDAO(): PlannedBusRouteInfoDAO

    companion object {
        @Volatile
        private var Instance: BusDatabase? = null

        fun getDatabase(context: Context): BusDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BusDatabase::class.java, "Bus_Database")
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it}
            }
        }
    }

}