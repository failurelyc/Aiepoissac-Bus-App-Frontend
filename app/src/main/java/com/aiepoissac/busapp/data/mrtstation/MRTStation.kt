package com.aiepoissac.busapp.data.mrtstation

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiepoissac.busapp.data.HasCoordinates

/**
 * This class contains information of a MRT station
 *
 * @param type MRT or LRT station
 * @param stationCode The unique station code of the station
 * @param stationName The name of the station
 * @param latitude The latitude of the station
 * @param longitude The longitude of the station
 */
@Entity(
    tableName = "MRT_Stations_Table"
)
data class MRTStation (
    val type: String,
    @PrimaryKey val stationCode: String,
    val stationName: String,
    override val latitude: Double,
    override val longitude: Double
): HasCoordinates {
    override fun toString(): String {
        return "$stationCode $stationName"
    }
}