package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusService(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String,
    @SerialName("NextBus") val nextBus: Bus,
    @SerialName("NextBus2") val nextBus2: Bus,
    @SerialName("NextBus3") val nextBus3: Bus
) {
    suspend fun attachOriginDestinationBusStopInfo(
        busRepository: BusRepository
    ): Pair<Pair<BusStopInfo?, BusStopInfo?>, BusService> {
        return Pair(
            first = Pair(
                first = nextBus.getOriginBusStopInfo(busRepository),
                second = nextBus.getDestinationBusStopInfo(busRepository)
            ),
            second = this
        )
    }
}
