package com.aiepoissac.busapp.data.busarrival

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Represent a bus stop that contains bus arrival data of operating bus services.
 *
 * @param metadata
 * @param busStopCode Bus stop reference code
 * @param services Bus services that are operating at this bus stop and time.
 */
@Serializable
data class BusStop (
    @SerialName("odata.metadata") val metadata: String = "",
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("Services") val services: List<BusService> = listOf()
) {
    /**
     * Get the bus arrivals of a specific bus service.
     *
     * @param serviceNo The bus service number
     * @return A list of bus services with this bus service number that is operating at this bus stop.
     * The list is empty if the bus service is not operating at this bus stop.
     * The list is of size two if the bus service serves this bus stop twice in different directions,
     * and both directions are operating.
     */
    fun getBusArrivalsOfASingleService(serviceNo: String): List<BusService> {
        //result is at most of size 2
        return services.filter { it.serviceNo == serviceNo }
    }
}

object DoubleStringSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DoubleToString", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        // Decoding as String and converting to Double
        val stringValue = decoder.decodeString()
        return stringValue.toDoubleOrNull() ?: 0.0 // fallback to 0.0 if conversion fails
    }

    override fun serialize(encoder: Encoder, value: Double) {
        // Default behavior: serialize the Double as String
        encoder.encodeString(value.toString())
    }
}

// Custom deserializer for LocalDateTime
object LocalDateTimeDeserializerForBusArrivals : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        // Get the string value and parse it using the formatter
        val stringValue = decoder.decodeString()
        try {
            return LocalDateTime.parse(stringValue, formatter)
        } catch (e: DateTimeParseException) {
            return LocalDateTime.MIN;
        }
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        // Serialize LocalDateTime to a String in the same pattern
        encoder.encodeString(value.format(formatter))
    }
}
