package com.aiepoissac.busapp.data.mrtstation

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

@Serializable
data class TrainServiceAlerts(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val value: TrainServiceAlertsInfo
)

/**
 * This class contains detailed information on train service unavailability during scheduled
 * operating hours, such as affected line and stations etc.
 *
 * @param status Indicates if train service is unavailable:
 * • 1 (for Normal Train Service) • 2 (for Disrupted Train Service)
 * @param affectedSegments information of a segment in the MRT network that is disrupted/delayed
 * @param message Travel advisory notification service for train commuters, published by LTA.
 */
@Serializable
data class TrainServiceAlertsInfo(
    @SerialName("Status") val status: Int,
    @SerialName("AffectedSegments") val affectedSegments: List<AffectedSegment>,
    @SerialName("Message") val message: List<TrainServiceAlertMessage>
)

/**
 * This class contains Travel advisory notification service for train commuters, published by LTA.
 *
 * @param content The message contents
 * @param createdDate The time this message was published
 */
@Serializable
data class TrainServiceAlertMessage(
    @SerialName("Content") val content: String,

    @Serializable(with = LocalDateTimeDeserializerForTrainServiceAlerts::class)
    @SerialName("CreatedDate") val createdDate: LocalDateTime
)

// Custom deserializer for LocalDateTime
object LocalDateTimeDeserializerForTrainServiceAlerts : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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