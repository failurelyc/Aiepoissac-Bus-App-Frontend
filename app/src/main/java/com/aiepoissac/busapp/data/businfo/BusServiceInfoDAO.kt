package com.aiepoissac.busapp.data.businfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BusServiceInfoDAO {

    @Insert
    suspend fun insert(busServiceInfo: BusServiceInfo)

    @Update
    suspend fun update(busServiceInfo: BusServiceInfo)

    @Delete
    suspend fun delete(busServiceInfo: BusServiceInfo)

    @Query("DELETE FROM Bus_Services_Table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM Bus_Services_Table")
    suspend fun getBusServicesCount(): Int

    @Query("SELECT * from Bus_Services_Table WHERE serviceNo LIKE :serviceNo || '%' AND " +
            "(LENGTH(serviceNo) = LENGTH(:serviceNo) OR SUBSTR(serviceNo, LENGTH(:serviceNo) + 1, 1) GLOB '[a-zA-Z]')")
    suspend fun getBusService(serviceNo: String): List<BusServiceInfo>

    @Query("SELECT * from Bus_Services_Table " +
            "WHERE serviceNo = :serviceNo AND direction = :direction")
    suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo?

}