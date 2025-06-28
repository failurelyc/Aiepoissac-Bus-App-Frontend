package com.aiepoissac.busapp.userdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BusJourneyListInfoDAO {

    @Insert
    suspend fun insert(busJourneyListInfo: BusJourneyListInfo)

    @Update
    suspend fun update(busJourneyListInfo: BusJourneyListInfo)

    @Delete
    suspend fun delete(busJourneyListInfo: BusJourneyListInfo)

    @Query("SELECT * FROM Bus_Journey_List_Info_Table")
    suspend fun getAllBusJourneyListInfo(): List<BusJourneyListInfo>

    @Query("SELECT * FROM Bus_Journey_List_Info_Table WHERE journeyID = :journeyID")
    suspend fun getBusJourneyListInfo(journeyID: String): BusJourneyListInfo

}