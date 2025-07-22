package com.aiepoissac.busapp

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.mrtstation.MRTStation

object GoogleMapsURLGenerator {

    private const val BASEURL = "https://www.google.com/maps"

    enum class TravelMode(val title: String) {
        Driving(title = "driving"),
        Walking(title = "walking"),
        Bicycling(title = "bicycling"),
        Transit(title = "transit")
    }

    fun directions(
        origin: HasCoordinates? = null,
        destination: HasCoordinates,
        travelMode: TravelMode
    ): String {
        val originCoordinates = origin?.getCoordinates()?.let { "${it.first}%2C${it.second}" }
        val destinationCoordinates = "${destination.getCoordinates().first}%2C${destination.getCoordinates().second}"

        return buildString {
            append("$BASEURL/dir/?api=1&")
            originCoordinates?.let { append("origin=$it&") }
            append("destination=$destinationCoordinates&")
            append("travelmode=${travelMode.title}")
        }
    }

    fun directionsToMRTStation(
        origin: HasCoordinates,
        destination: MRTStation,
        travelMode: TravelMode = TravelMode.Transit
    ) : String {

        return "$BASEURL/dir/?api=1&" +
                "origin=${origin.getCoordinates().first}%2C${origin.getCoordinates().second}&" +
                "destination=${destination.stationName}+station&" +
                "travelmode=${travelMode.title}"
    }



}