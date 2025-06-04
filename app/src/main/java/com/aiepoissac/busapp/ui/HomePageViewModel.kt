package com.aiepoissac.busapp.ui

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.busarrival.getBusArrival
import com.aiepoissac.busapp.data.businfo.populateBusRoutes
import com.aiepoissac.busapp.data.businfo.populateBusServices
import com.aiepoissac.busapp.data.businfo.populateBusStops
import com.aiepoissac.busapp.data.businfo.populateMRTStations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class HomePageViewModel : ViewModel() {

    var busArrivalBusStopCodeInput by mutableStateOf("")
        private set

    var busServiceNoInput by mutableStateOf("")
        private set

    var downloaded by mutableStateOf(false)
        private set

    var failedDownload by mutableStateOf(false)
        private set

    fun updateBusArrivalBusStopCodeInput(input: String) {
        if (input.length <= 5) {
            this.busArrivalBusStopCodeInput = input
        }
    }

    fun updateBusServiceNoInput(input: String) {
        if (input.length <= 4) {
            this.busServiceNoInput = input
        }
    }

    init {
        reInitialiseData()
    }

    fun reInitialiseData() {
        failedDownload = false
        downloaded = false
        viewModelScope.launch {
            initialiseOfflineData()
            initialiseOnlineData()
        }
    }

    private suspend fun initialiseOfflineData() = withContext(Dispatchers.IO) {
        if (BusApplication.instance.container.busRepository.getMRTStationCount() == 0) {
            populateMRTStations(BusApplication.instance.container.busRepository)
        }
    }

    private suspend fun initialiseOnlineData() = withContext(Dispatchers.IO) {
        if (checkIfSundayOrMonday4amPassed()) {
            try {
                val busRepository = BusApplication.instance.container.busRepository
                Log.d("BusApplication", "Checking connection with API")
                getBusArrival("11111")
                Log.d("BusApplication", "Connection is successful")
                Log.d("BusApplication", "Started Downloading Bus Data")
                populateBusServices(busRepository)
                populateBusRoutes(busRepository)
                populateBusStops(busRepository)
                Log.d("BusApplication", "Downloaded Bus Data")
                if (busRepository.getBusServicesCount() != 0 &&
                    busRepository.getBusRoutesCount() != 0 &&
                    busRepository.getBusStopsCount() != 0) {
                    withContext(Dispatchers.Main) {
                        downloaded = true
                    }
                    saveLastOpenedTime()
                } else {
                    throw IOException("Nothing was downloaded")
                }
            } catch (e: IOException) {
                Log.e("BusApplication", "Failed to download bus data")
                withContext(Dispatchers.Main) {
                    failedDownload = true
                }
            }
        } else {
            Log.d("BusApplication", "Bus Data already downloaded")
            withContext(Dispatchers.Main) {
                downloaded = true
            }
            saveLastOpenedTime()
        }
    }

    private fun saveLastOpenedTime() {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val lastOpenedTime = System.currentTimeMillis()
        editor.putLong("last_opened_time", lastOpenedTime)
        editor.apply()
    }

    private fun getLastOpenedTime(): Long {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("last_opened_time", 0)
    }

    private fun checkIfSundayOrMonday4amPassed(): Boolean {
        val lastOpenedTime = getLastOpenedTime()
        val lastDate = Calendar.getInstance()
        lastDate.timeInMillis = lastOpenedTime
        val thisDate = Calendar.getInstance()
        thisDate.timeInMillis = System.currentTimeMillis()
        val dayOfLastOpenedDate = lastDate.get(Calendar.DAY_OF_WEEK)
        if (dayOfLastOpenedDate == Calendar.SUNDAY) {
            val hourOfLastOpenTime = lastDate.get(Calendar.HOUR_OF_DAY)
            lastDate.set(Calendar.HOUR_OF_DAY, 4)
            lastDate.set(Calendar.MINUTE, 0)
            lastDate.set(Calendar.SECOND, 0)
            if (hourOfLastOpenTime >= 4) {
                lastDate.add(Calendar.DAY_OF_YEAR, 1) // Set to coming Monday 4am
            }
        } else if (dayOfLastOpenedDate == Calendar.MONDAY) {
            val hourOfLastOpenTime = lastDate.get(Calendar.HOUR_OF_DAY)
            lastDate.set(Calendar.HOUR_OF_DAY, 4)
            lastDate.set(Calendar.MINUTE, 0)
            lastDate.set(Calendar.SECOND, 0)
            if (hourOfLastOpenTime >= 4) {
                lastDate.add(Calendar.DAY_OF_YEAR, 6) // Set to coming Sunday 4am
            }
        } else { //dayOfLastDate is not Sunday or Monday
            val daysFromLastDateToSunday = (Calendar.SUNDAY - dayOfLastOpenedDate + 7) % 7
            lastDate.add(Calendar.DAY_OF_YEAR, daysFromLastDateToSunday)
            lastDate.set(Calendar.HOUR_OF_DAY, 4)
            lastDate.set(Calendar.MINUTE, 0)
            lastDate.set(Calendar.SECOND, 0)
        }
        return thisDate.after(lastDate)
    }

}


