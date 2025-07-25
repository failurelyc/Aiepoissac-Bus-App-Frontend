package com.aiepoissac.busapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {

    val currentLocation: StateFlow<Location?>

    fun startFetchingLocation(fastRefresh: Boolean)

    fun stopFetchingLocation()

}

class RealLocationRepository : LocationRepository {

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

    private val _currentLocation = MutableStateFlow<Location?>(null)
    override val currentLocation: StateFlow<Location?> = _currentLocation

    private var isFetchingLocation = false

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                _currentLocation.value = location
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startFetchingLocation(fastRefresh: Boolean) {
        if (!isFetchingLocation) {
            fusedLocationClient.requestLocationUpdates(
                if (fastRefresh) fastLocationRequest else slowLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            isFetchingLocation = true
        }

    }

    override fun stopFetchingLocation() {
        if (isFetchingLocation) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isFetchingLocation = false
        }
    }

}