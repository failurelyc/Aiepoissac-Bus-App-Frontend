package com.aiepoissac.busapp.ui


import com.aiepoissac.busapp.data.businfo.BusServiceInfo

data class BusServiceUIState (
    val busServiceList: List<BusServiceInfo> = listOf(),
    val busServiceNoInput: String = ""
)