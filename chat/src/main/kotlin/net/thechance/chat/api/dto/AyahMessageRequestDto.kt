package net.thechance.chat.api.dto

import net.thechance.chat.service.model.MessageAyahRequestArgs
import java.util.UUID

data class AyahMessageRequestDto(
    val messageId: UUID,
    val chatId: UUID,
    val ayahNumber: Int,
    val suraNumber: Int,
    val ayahText: String
)

fun AyahMessageRequestDto.toRequestArgs(senderId: UUID): MessageAyahRequestArgs{
    return MessageAyahRequestArgs(
        messageId = messageId,
        chatId = chatId,
        senderId = senderId,
        ayahNumber = ayahNumber,
        suraNumber = suraNumber,
        ayahText = ayahText
    )
}
