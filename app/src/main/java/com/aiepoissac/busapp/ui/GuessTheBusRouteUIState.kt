package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo

data class GuessTheBusRouteUIState(
    val score: Int = 0,
    val count: Int = 0,
    val streak: Int = 0,
    val highestStreak: Int = 0,
    val busRoute: List<BusRouteInfoWithBusStopInfo> = listOf(),
    val choices: List<BusServiceInfoWithBusStopInfo> = listOf(),
    val correctChoice: BusServiceInfoWithBusStopInfo? = null,
    val selected: BusServiceInfoWithBusStopInfo? = null,
    val difficulty: Int = 0,
    val difficultyInput: Float = 0.0f,
    val showAnswer: Boolean = false,
    val timeLeft: Long = Long.MAX_VALUE,
    val hideMap: Boolean = false
)