package com.aiepoissac.busapp.data

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * This interface represents a location with latitude and longitude coordinates.
 */
interface HasCoordinates {

    val latitude: Double
    val longitude: Double

    /**
     * Get the coordinates of this location.
     *
     * @return A pair containing the latitude and longitude of this location
     */
    fun getCoordinates(): Pair<Double, Double> {
        return Pair(latitude, longitude)
    }

    /**
     * Get the distance between this location and the specified location
     *
     * @param other The specified location
     * @return The distance in metres
     */
    fun distanceFromInMetres(other: HasCoordinates): Int {
        val point1 = this.getCoordinates()
        val point2 = other.getCoordinates()
        return distanceBetweenInMetres(point1, point2)
    }

}

private fun distanceBetweenInMetres(point1: Pair<Double, Double>, point2: Pair<Double, Double>): Int {
    return (6377830 * acos(
        sin(Math.toRadians(point1.first)) * sin(Math.toRadians(point2.first)) +
                cos(Math.toRadians(point1.first)) * cos(Math.toRadians(point2.first)) *
                cos(Math.toRadians(point2.second - point1.second))
    )).toInt()
}