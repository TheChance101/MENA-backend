package net.thechance.chat.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class MessageContent {

    @Serializable
    @SerialName("TEXT")
    data class Text(val text: String) : MessageContent()

    @Serializable
    @SerialName("IMAGE")
    data class Image(val url: String) : MessageContent()

    @Serializable
    @SerialName("AUDIO")
    data class Audio(val url: String, val durationMs: Long) : MessageContent()

    @Serializable
    @SerialName("AYAH")
    data class Ayah(val ayahNumber: Int, val suraNumber:Int, val ayahText: String): MessageContent()

    @Serializable
    @SerialName("MONEY")
    data class Money(val amount: Double) : MessageContent()
}