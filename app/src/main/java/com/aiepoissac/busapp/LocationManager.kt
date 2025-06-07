package com.aiepoissac.busapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationManager {

    const val REFRESH_INTERVAL_IN_SECONDS = 5

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(BusApplication.instance)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        REFRESH_INTERVAL_IN_SECONDS * 1000L
    )
        .build()

    var currentLocation = mutableStateOf<Location?>(null)
        private set

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                currentLocation.value = location
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startFetchingLocation() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    fun stopFetchingLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}