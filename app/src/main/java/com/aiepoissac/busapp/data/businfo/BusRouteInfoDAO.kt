package com.aiepoissac.busapp.data.businfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface BusRouteInfoDAO {

    @Insert
    suspend fun insert(busRouteInfo: BusRouteInfo)

    @Update
    suspend fun update(busRouteInfo: BusRouteInfo)

    @Delete
    suspend fun delete(busRouteInfo: BusRouteInfo)

    @Query("DELETE FROM Bus_Routes_Table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM Bus_Routes_Table")
    suspend fun getBusRoutesCount(): Int

    @Transaction
    @Query("SELECT * FROM Bus_Routes_Table WHERE serviceNo = :serviceNo AND direction = :direction")
    suspend fun getBusServiceRoute(serviceNo: String, direction: Int):
            List<BusRouteInfoWithBusStopInfo>

    @Query("SELECT COUNT(*) FROM Bus_Routes_Table WHERE serviceNo = :serviceNo AND direction = :direction")
    suspend fun getBusServiceRouteLength(serviceNo: String, direction: Int): Int

    @Transaction
    @Query("SELECT * FROM Bus_Routes_Table WHERE " +
            "serviceNo = :serviceNo AND direction = :direction AND stopSequence >= :stopSequence")
    suspend fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String, direction: Int, stopSequence: Int
    ): List<BusRouteInfoWithBusStopInfo>

    @Transaction
    @Query("SELECT * FROM Bus_Routes_Table WHERE " +
            "serviceNo = :serviceNo AND direction = :direction AND stopSequence == :stopSequence")
    suspend fun getBusRouteInfoWithBusStopInfo(
        serviceNo: String, direction: Int, stopSequence: Int
    ): BusRouteInfoWithBusStopInfo

    @Query("SELECT * FROM Bus_Routes_Table WHERE busStopCode = :busStopCode")
    suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo>
}