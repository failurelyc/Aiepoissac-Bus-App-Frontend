package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.HasCoordinates
import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo
import com.aiepoissac.busapp.data.businfo.MRTStation

data class NearbyUIState(
    val busStopList: List<Pair<Int, BusStopInfoWithBusRoutesInfo>> = listOf(),
    val mrtStationList: List<Pair<Int, MRTStation>> = listOf(),
    val point: HasCoordinates,
    val distanceThreshold: Int
)
