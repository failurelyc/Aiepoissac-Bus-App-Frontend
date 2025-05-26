package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo

data class NearbyUIState(
    val busStopList: List<Pair<Int, BusStopInfoWithBusRoutesInfo>> = listOf(),
    val distanceThreshold: Int
)
