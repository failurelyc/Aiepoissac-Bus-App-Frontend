package com.aiepoissac.busapp.userdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Bus_Journey_List_Info_Table"
)
data class JourneyInfo (
    @PrimaryKey val journeyID: String,
    val description: String
)