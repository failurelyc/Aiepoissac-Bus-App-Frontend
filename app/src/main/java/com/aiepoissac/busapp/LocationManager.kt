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

    const val FAST_REFRESH_INTERVAL_IN_SECONDS = 2

    const val SLOW_REFRESH_INTERVAL_IN_SECONDS = 10

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(BusApplication.instance)

    private val fastLocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        FAST_REFRESH_INTERVAL_IN_SECONDS * 1000L
    )
        .build()

    private val slowLocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        SLOW_REFRESH_INTERVAL_IN_SECONDS * 1000L
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
    fun startFetchingLocation(fastRefresh: Boolean) {
        fusedLocationClient.requestLocationUpdates(
            if (fastRefresh) fastLocationRequest else slowLocationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    fun stopFetchingLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}