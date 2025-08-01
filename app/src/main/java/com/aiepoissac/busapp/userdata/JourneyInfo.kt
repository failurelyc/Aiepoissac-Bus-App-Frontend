package com.aiepoissac.busapp.userdata

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class contains information of a Bus Journey.
 *
 * @param journeyID The unique ID of this journey
 * @param description The name of this journey
 */
@Entity(
    tableName = "Bus_Journey_List_Info_Table"
)
data class JourneyInfo (
    @PrimaryKey val journeyID: String,
    val description: String
)