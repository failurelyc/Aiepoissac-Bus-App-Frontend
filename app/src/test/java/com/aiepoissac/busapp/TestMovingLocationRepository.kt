package com.aiepoissac.busapp

import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TestMovingLocationRepository : LocationRepository {

    private val _currentLocation = MutableStateFlow<Location?>(null)
    override val currentLocation: StateFlow<Location?> = _currentLocation

    private var job: Job? = null

    override fun startFetchingLocation(fastRefresh: Boolean) {
        job?.cancel()

        job = CoroutineScope(Dispatchers.Default).launch {
            val startLat = 1.2948117103061263
            val startLon = 103.78437918054274
            val endLat = 1.297360034928917
            val endLon = 103.78095045621575

            val steps = 10  // number of intermediate points (updates)
            val intervalMs = 2000L  // 2 second per step
            val totalDistance = floatArrayOf(0f)
            Location.distanceBetween(startLat, startLon, endLat, endLon, totalDistance)

            for (i in 0..steps) {
                val fraction = i.toFloat() / steps
                val lat = interpolate(startLat, endLat, fraction)
                val lon = interpolate(startLon, endLon, fraction)

                val location = Location("mock_provider").apply {
                    latitude = lat
                    longitude = lon
                    accuracy = 5.0f
                    time = 1_000_000_000_000L
                    speed = (totalDistance[0] / steps) / (intervalMs / 1000f) // meters/second
                }

                _currentLocation.emit(location)
                delay(intervalMs)
            }
        }
    }

    override fun stopFetchingLocation() {
        job?.cancel()
        job = null
    }

    private fun interpolate(start: Double, end: Double, fraction: Float): Double {
        return (1 - fraction) * start + fraction * end
    }
}

class StationaryLocationRepository : LocationRepository {

    private val location = Location("mock_provider").apply {
        latitude = 1.2948117103061263
        longitude = 103.78437918054274
        accuracy = 5.0f
        speed = 0.0f
        time = 1_000_000_000_000L
    }

    private val _currentLocation = MutableStateFlow<Location?>(location)
    override val currentLocation: StateFlow<Location?> = _currentLocation

    override fun startFetchingLocation(fastRefresh: Boolean) {

    }

    override fun stopFetchingLocation() {

    }
}