package com.aiepoissac.busapp.ui

import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo

data class BusServiceUIState (
    val busServiceList: List<BusServiceInfoWithBusStopInfo> = listOf(),
    val busServiceNoInput: String = ""
)