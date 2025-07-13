package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiepoissac.busapp.data.HasCoordinates

@Entity(
    tableName = "MRT_Stations_Table"
)
data class MRTStation (
    val type: String,
    @PrimaryKey val stationCode: String,
    val stationName: String,
    override val latitude: Double,
    override val longitude: Double
): HasCoordinates