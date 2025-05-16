package com.aiepoissac.busapp.test

import com.aiepoissac.busapp.data.busarrival.getBusArrival
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println(getBusArrival(34009))
}