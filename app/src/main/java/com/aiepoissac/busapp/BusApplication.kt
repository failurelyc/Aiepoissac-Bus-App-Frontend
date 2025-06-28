package com.aiepoissac.busapp

import android.app.Application
import com.aiepoissac.busapp.data.AppContainer
import com.aiepoissac.busapp.data.AppDataContainer

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
    }

}