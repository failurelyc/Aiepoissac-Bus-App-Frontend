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
    val latitude: Double,
    val longitude: Double
): HasCoordinates {
    override fun getCoordinates(): Pair<Double, Double> {
        return Pair(latitude, longitude)
    }

}