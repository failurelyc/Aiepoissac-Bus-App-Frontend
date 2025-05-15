package com.aiepoissac.busapp.data.busarrival

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusService(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String,
    @SerialName("NextBus") val nextBus: Bus,
    @SerialName("NextBus2") val nextBus2: Bus,
    @SerialName("NextBus3") val nextBus3: Bus
)
