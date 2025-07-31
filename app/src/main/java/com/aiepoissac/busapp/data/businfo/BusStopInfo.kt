package com.aiepoissac.busapp.data.businfo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiepoissac.busapp.data.HasCoordinates
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusStopsInfo(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: List<BusStopInfo>
)

/**
 * This class contains information of a bus stop, including location coordinates
 *
 * @param busStopCode The unique 5-digit identifier for this physical bus stop
 * @param roadName The road on which this bus stop is located
 * @param description Landmarks next to the bus stop (if any) to aid in identifying this bus stop
 * @param latitude Location coordinates for this bus stop
 * @param longitude Location coordinates for this bus stop
 */
@Entity(
    tableName = "Bus_Stops_Table"
)
@Serializable
data class BusStopInfo (
    @PrimaryKey
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("RoadName") val roadName: String,
    @SerialName("Description") val description: String,
    @SerialName("Latitude") override val latitude: Double,
    @SerialName("Longitude") override val longitude: Double
) : HasCoordinates {

    override fun toString(): String {
        return "$busStopCode $description"
    }

}

/**
 * Finds nearby bus stops within a circle centred on a point.
 *
 * @param point The centre of the circle
 * @param busRepository the repository of the bus data
 * @param distanceThreshold The radius of the circle
 * @return A list of bus stops within this circle, as well as the distances between the point and each bus stop.
 */
suspend fun findNearbyBusStops(
    point: HasCoordinates,
    busRepository: BusRepository,
    distanceThreshold: Int
): List<Pair<Int, BusStopInfo>> {
    return busRepository
        .getAllBusStops()
        .map { Pair(it.distanceFromInMetres(point), it) }
        .filter { it.first < distanceThreshold }
        .sortedBy { it.first }
}

