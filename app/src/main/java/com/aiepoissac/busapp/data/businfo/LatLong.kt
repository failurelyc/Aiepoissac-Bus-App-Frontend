package com.aiepoissac.busapp.data.businfo

import com.aiepoissac.busapp.data.HasCoordinates

data class LatLong (
    val latitude: Double,
    val longitude: Double
) : HasCoordinates {
    override fun getCoordinates(): Pair<Double, Double> {
        return Pair(latitude, longitude)
    }

}