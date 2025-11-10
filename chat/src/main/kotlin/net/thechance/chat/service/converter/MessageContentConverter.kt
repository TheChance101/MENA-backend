package net.thechance.chat.service.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.chat.service.exception.MalformedMessageContentException
import net.thechance.chat.service.model.MessageContent

@Converter(autoApply = true)
class MessageContentConverter: AttributeConverter<MessageContent, String> {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "type"
    }

    override fun convertToDatabaseColumn(attribute: MessageContent): String {
        return try {
            println("\n\n$attribute\n\n")
            json.encodeToString<MessageContent>(attribute)
        } catch (e: Exception) {
            throw MalformedMessageContentException("Failed to serialize message content before saving: ${e.message}")
        }
    }

    override fun convertToEntityAttribute(dbData: String): MessageContent {
        return try {
            json.decodeFromString<MessageContent>(dbData)
        } catch (e: Exception) {
            throw MalformedMessageContentException("Failed to deserialize stored message content: ${e.message}")
        }
    }
}