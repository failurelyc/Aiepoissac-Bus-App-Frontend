package com.aiepoissac.busapp.data.mrtstation

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MRTStationDAO {

    @Insert
    suspend fun insert(mrtStation: MRTStation)

    @Update
    suspend fun update(mrtStation: MRTStation)

    @Delete
    suspend fun delete(mrtStation: MRTStation)

    @Query("DELETE FROM MRT_Stations_Table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM MRT_Stations_Table")
    suspend fun getMRTStationCount(): Int

    @Query("SELECT * FROM MRT_Stations_Table WHERE stationCode = :stationCode")
    suspend fun getMRTStation(stationCode: String): MRTStation

    @Query("SELECT * FROM MRT_Stations_Table " +
            "WHERE stationName LIKE '%' || :partialName || '%'")
    suspend fun getMRTStationsContaining(partialName: String): List<MRTStation>

    @Query("SELECT * FROM MRT_Stations_Table")
    suspend fun getAllMRTStations(): List<MRTStation>

}