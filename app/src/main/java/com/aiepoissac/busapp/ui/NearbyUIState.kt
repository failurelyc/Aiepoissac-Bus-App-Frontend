package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusStopInfo

data class NearbyUIState(
    val busStopList: List<Pair<Int, BusStopInfo>> = listOf(),
    val distanceThreshold: Int
)
