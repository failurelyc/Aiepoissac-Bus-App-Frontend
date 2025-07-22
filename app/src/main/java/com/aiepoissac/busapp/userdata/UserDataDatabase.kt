package com.aiepoissac.busapp.userdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [JourneySegmentInfo::class, JourneyInfo::class],
    version = 1,
    exportSchema = false
)
abstract class UserDataDatabase: RoomDatabase() {

    abstract fun busJourneyInfoDAO(): JourneySegmentInfoDAO

    abstract fun busJourneyListInfoDAO(): JourneyInfoDAO

    companion object {
        @Volatile
        private var Instance: UserDataDatabase? = null

        fun getDatabase(context: Context): UserDataDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, UserDataDatabase::class.java, "User_Data_Database")
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }

}