package com.aiepoissac.busapp.data.businfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface BusStopInfoDAO {

    @Insert
    suspend fun insert(busStopInfo: BusStopInfo)

    @Update
    suspend fun update(busStopInfo: BusStopInfo)

    @Delete
    suspend fun delete(busStopInfo: BusStopInfo)

    @Query("DELETE FROM Bus_Stops_Table")
    suspend fun deleteAll()

    @Query("SELECT * FROM Bus_Stops_Table WHERE busStopCode = :busStopCode")
    suspend fun getBusStop(busStopCode: String): BusStopInfo?

    @Query("SELECT * FROM Bus_Stops_Table")
    suspend fun getAllBusStops(): List<BusStopInfo>

    @Transaction
    @Query("SELECT * FROM Bus_Stops_Table WHERE busStopCode = :busStopCode")
    suspend fun getBusStopWithBusRoutes(busStopCode: String): BusStopInfoWithBusRoutesInfo

}