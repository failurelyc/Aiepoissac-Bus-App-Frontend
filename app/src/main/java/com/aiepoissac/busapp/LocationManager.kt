package com.aiepoissac.busapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

interface LocationManager {



    var currentLocation: MutableState<Location?>

    fun startFetchingLocation(fastRefresh: Boolean)

    fun stopFetchingLocation()

}

class RealLocationManager : LocationManager {

    private val fastRefreshInterval = 2

    private val slowRefreshInterval = 10

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(BusApplication.instance)

    private val fastLocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        fastRefreshInterval * 1000L
    )
        .build()

    private val slowLocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        slowRefreshInterval * 1000L
    )
        .build()

    override var currentLocation = mutableStateOf<Location?>(null)

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                currentLocation.value = location
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startFetchingLocation(fastRefresh: Boolean) {
        fusedLocationClient.requestLocationUpdates(
            if (fastRefresh) fastLocationRequest else slowLocationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    override fun stopFetchingLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}