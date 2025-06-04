package com.aiepoissac.busapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationManager {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(BusApplication.instance)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 10000)
        .build()

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                currentLocation.value = location
            }
        }
    }

    var currentLocation = mutableStateOf<Location?>(null)
        private set

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