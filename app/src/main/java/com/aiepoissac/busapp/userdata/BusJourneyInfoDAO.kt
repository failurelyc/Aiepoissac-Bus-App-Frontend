package com.aiepoissac.busapp.userdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BusJourneyInfoDAO {

    @Insert
    suspend fun insert(busJourney: BusJourneyInfo)

    @Update
    suspend fun update(busJourney: BusJourneyInfo)

    @Delete
    suspend fun delete(busJourney: BusJourneyInfo)

    @Query("DELETE FROM Bus_Journey_Info_Table WHERE journeyID = :journeyID")
    suspend fun deleteBusJourneyList(journeyID: String)

    @Query("SELECT * FROM Bus_Journey_Info_Table WHERE journeyID = :journeyID")
    suspend fun getBusJourneyList(journeyID: String): List<BusJourneyInfo>

}