package com.aiepoissac.busapp.data.businfo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BusServiceInfo::class, BusRouteInfo::class, BusStopInfo::class],
    version = 1, exportSchema = false)
abstract class BusDatabase: RoomDatabase() {

    abstract fun busServiceInfoDao(): BusServiceInfoDAO

    abstract fun busRouteInfoDAO(): BusRouteInfoDAO

    abstract fun busStopInfoDAO(): BusStopInfoDAO

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