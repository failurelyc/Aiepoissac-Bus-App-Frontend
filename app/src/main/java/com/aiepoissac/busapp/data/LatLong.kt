package com.aiepoissac.busapp.data

data class LatLong (
    override val latitude: Double,
    override val longitude: Double
) : HasCoordinates