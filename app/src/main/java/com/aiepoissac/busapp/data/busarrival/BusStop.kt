package com.aiepoissac.busapp.data.busarrival

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Scanner

@Serializable
data class BusStop (
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("Services") val services: List<BusService>
)

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
object LocalDateTimeDeserializer : KSerializer<LocalDateTime> {
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

suspend fun getBusArrival(busStopCode: Int): BusStop {
    val json = withContext(Dispatchers.IO) {
        getBusArrivalData(busStopCode) // e.g. network or file call
    }
    return Json.decodeFromString<BusStop>(json)
}

private suspend fun getBusArrivalData(busStopCode: Int): String = withContext(Dispatchers.IO) {
    val url = URL("https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival?BusStopCode=$busStopCode")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("AccountKey", "***REMOVED***")
    connection.setRequestProperty("accept", "application/json")
    connection.connect()

    if (connection.responseCode != 200) {
        throw IOException(connection.responseMessage)
    } else {
        val result = StringBuilder()
        val scanner = Scanner(connection.inputStream)
        while (scanner.hasNext()) {
            result.append(scanner.nextLine())
        }
        scanner.close()
        return@withContext result.toString()
        }
}
