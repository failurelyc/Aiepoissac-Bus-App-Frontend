package com.aiepoissac.busapp.data

/**
 * This class represents a location with latitude and longitude coordinates.
 *
 * @param latitude The latitude of the location coordinates
 * @param longitude The longitude of the location coordinates
 */
data class LatLong (
    override val latitude: Double,
    override val longitude: Double
) : HasCoordinates