package com.aiepoissac.busapp.data.busarrival

import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class represents a bus service operating at a bus stop.
 *
 * @param serviceNo Bus service number
 * @param operator Public Transport Operator Codes:
 * • SBST (for SBS Transit)
 * • SMRT (for SMRT Corporation)
 * • TTS (for Tower Transit Singapore)
 * • GAS (for Go Ahead Singapore)
 * @param nextBus The next incoming bus. This bus is guaranteed to be valid (operating).
 * @param nextBus2 The next next incoming bus.
 * @param nextBus3 The next next next incoming bus.
 */
@Serializable
data class BusService(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operator: String,
    @SerialName("NextBus") val nextBus: Bus,
    @SerialName("NextBus2") val nextBus2: Bus,
    @SerialName("NextBus3") val nextBus3: Bus
) {

    /**
     * Returns a data structure that combines the bus arrival data of a bus service with
     * the origin and destination BusStopInfo
     *
     * @param busRepository the repository of the bus data
     */
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
