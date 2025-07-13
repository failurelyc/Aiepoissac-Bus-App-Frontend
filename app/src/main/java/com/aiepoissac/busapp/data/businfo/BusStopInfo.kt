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

suspend fun findNearbyBusStops(
    point: HasCoordinates,
    busRepository: BusRepository,
    distanceThreshold: Int,
    limit: Int = Int.MAX_VALUE
): List<Pair<Int, BusStopInfo>> {
    return busRepository
        .getAllBusStops()
        .map { Pair(it.distanceFromInMetres(point), it) }
        .filter { it.first < distanceThreshold }
        .sortedBy { it.first }
        .take(limit)
}

