package com.aiepoissac.busapp.userdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface JourneySegmentInfoDAO {

    @Insert
    suspend fun insert(journeySegmentInfo: JourneySegmentInfo)

    @Update
    suspend fun update(journeySegmentInfo: JourneySegmentInfo)

    @Delete
    suspend fun delete(journeySegmentInfo: JourneySegmentInfo)

    @Query("DELETE FROM Bus_Journey_Info_Table WHERE journeyID = :journeyID")
    suspend fun deleteAllJourneySegments(journeyID: String)

    @Query("SELECT * FROM Bus_Journey_Info_Table WHERE journeyID = :journeyID")
    suspend fun getAllJourneySegments(journeyID: String): List<JourneySegmentInfo>

}