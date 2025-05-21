package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusRouteInfo

data class BusRouteUIState (
    val busRoute: List<BusRouteInfo> = listOf()
)