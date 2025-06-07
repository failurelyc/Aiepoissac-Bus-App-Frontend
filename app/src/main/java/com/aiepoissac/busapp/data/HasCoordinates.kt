package com.aiepoissac.busapp.data

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

interface HasCoordinates {

    fun getCoordinates(): Pair<Double, Double> //[latitude, longitude]

    fun distanceFromInMetres(other: HasCoordinates): Int {
        val point1 = this.getCoordinates()
        val point2 = other.getCoordinates()
        return distanceBetweenInMetres(point1, point2)
    }

//    fun distanceFromInMetres(point2: Pair<Double, Double>): Int {
//        val point1 = this.getCoordinates()
//        return distanceBetweenInMetres(point1, point2)
//    }
}

private fun distanceBetweenInMetres(point1: Pair<Double, Double>, point2: Pair<Double, Double>): Int {
    return (6377830 * acos(
        sin(Math.toRadians(point1.first)) * sin(Math.toRadians(point2.first)) +
                cos(Math.toRadians(point1.first)) * cos(Math.toRadians(point2.first)) *
                cos(Math.toRadians(point2.second - point1.second))
    )).toInt()
}