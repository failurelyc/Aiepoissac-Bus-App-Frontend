package com.aiepoissac.busapp.data.businfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PlannedBusRouteInfoDAO {

    @Insert
    suspend fun insert(plannedBusRouteInfo: PlannedBusRouteInfo)

    @Update
    suspend fun update(plannedBusRouteInfo: PlannedBusRouteInfo)

    @Delete
    suspend fun delete(plannedBusRouteInfo: PlannedBusRouteInfo)

    @Query("DELETE FROM Planned_Bus_Routes_Table")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM Planned_Bus_Routes_Table")
    suspend fun getAll(): List<PlannedBusRouteInfoWithBusStopInfo>

}