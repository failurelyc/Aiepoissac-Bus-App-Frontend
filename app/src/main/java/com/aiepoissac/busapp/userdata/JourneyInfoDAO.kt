package com.aiepoissac.busapp.userdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface JourneyInfoDAO {

    @Insert
    suspend fun insert(journeyInfo: JourneyInfo)

    @Update
    suspend fun update(journeyInfo: JourneyInfo)

    @Delete
    suspend fun delete(journeyInfo: JourneyInfo)

    @Query("SELECT * FROM Bus_Journey_List_Info_Table")
    suspend fun getAllJourneys(): List<JourneyInfo>

    @Query("SELECT * FROM Bus_Journey_List_Info_Table WHERE journeyID = :journeyID")
    suspend fun getJourney(journeyID: String): JourneyInfo

}