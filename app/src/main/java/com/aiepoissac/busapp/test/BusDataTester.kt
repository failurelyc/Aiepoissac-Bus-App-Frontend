package com.aiepoissac.busapp.test


import com.aiepoissac.busapp.data.businfo.BusDataType
import com.aiepoissac.busapp.data.businfo.getData
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    //println(getData(BusDataType.BusServices, 0))
    println(getData(BusDataType.BusRoutes, 0))
    //println(getData(BusDataType.BusStops, 0))
}