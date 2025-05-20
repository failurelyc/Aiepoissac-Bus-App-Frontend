package com.aiepoissac.busapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aiepoissac.busapp.ui.BusApp
import com.aiepoissac.busapp.ui.theme.AiepoissacBusAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiepoissacBusAppTheme {
                BusApp()
            }
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


