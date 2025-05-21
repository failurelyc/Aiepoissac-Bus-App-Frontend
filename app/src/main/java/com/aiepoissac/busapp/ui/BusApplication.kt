package com.aiepoissac.busapp.ui

import android.app.Application
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import com.aiepoissac.busapp.data.AppContainer
import com.aiepoissac.busapp.data.AppDataContainer
import com.aiepoissac.busapp.data.businfo.populateBusServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.system.exitProcess

class BusApplication : Application() {

    companion object {

        lateinit var instance: BusApplication
            private set

    }

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(context = this)
        instance = this

        MainScope().launch {
            initialiseData()
            saveLastOpenedTime()
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        MainScope().cancel()
    }

    private suspend fun initialiseData() = withContext(Dispatchers.IO) {
        if (checkIfSundayOrMonday4amPassed()) {
            try {
                Log.w("BusApplication", "Started Downloading Bus Data")
                populateBusServices(BusApplication.instance.container.busRepository)
                Log.w("BusApplication", "Downloaded Bus Data")
            } catch (e: IOException) {
                Log.w("BusApplication", "Failed to download bus data")
                exitProcess(1)
            }
        } else {
            Log.w("BusApplication", "Bus Data already downloaded")
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