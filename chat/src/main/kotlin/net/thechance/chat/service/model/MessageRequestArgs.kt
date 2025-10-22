package net.thechance.chat.service.model

import net.thechance.chat.api.dto.MessageRequestDto
import java.util.*


data class MessageRequestArgs(
    val chatId: UUID,
    val senderId: UUID,
    val text: String?,
    val messageId: UUID?
)

fun MessageRequestDto.toRequestArgs(senderId: UUID): MessageRequestArgs {
    return MessageRequestArgs(
        chatId = this.chatId,
        senderId = senderId,
        text = this.text,
        messageId = this.messageId
    )
}