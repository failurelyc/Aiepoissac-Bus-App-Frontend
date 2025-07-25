package com.aiepoissac.busapp

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.aiepoissac.busapp.ui.BusApp
import com.aiepoissac.busapp.ui.theme.AiepoissacBusAppTheme
import com.google.maps.android.ktx.BuildConfig


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Box(Modifier.safeDrawingPadding()) {
                AiepoissacBusAppTheme {
                    BusApp()
                }
            }
        }

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyDialog() // or penaltyDeath(), penaltyDialog(), penaltyFlashScreen()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog() // or penaltyDeath()
                    .build()
            )
        }
    }

//    override fun onStart() {
//        super.onStart()
//        Log.d(TAG, "onStart Called")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(TAG, "onResume Called")
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        Log.d(TAG, "onRestart Called")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.d(TAG, "onPause Called")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.d(TAG, "onStop Called")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG, "onDestroy Called")
//    }


}


