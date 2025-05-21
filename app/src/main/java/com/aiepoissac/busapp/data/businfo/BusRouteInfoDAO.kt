package com.aiepoissac.busapp.data.businfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * from Bus_Routes_Table WHERE serviceNo = :serviceNo AND direction = :direction")
    fun getBusServiceRoute(serviceNo: String, direction: Int): Flow<List<BusRouteInfo>>

    @Query("SELECT * from Bus_Routes_Table WHERE " +
            "serviceNo = :serviceNo AND direction = :direction AND stopSequence >= :stopSequence")
    fun getBusServiceRouteAfterSpecifiedStop(serviceNo: String, direction: Int, stopSequence: Int):
            Flow<List<BusRouteInfo>>

    @Query("SELECT * from Bus_Routes_Table WHERE busStopCode = :busStopCode")
    fun getBusRoutesAtBusStop(busStopCode: String): Flow<List<BusRouteInfo>>
}