package net.thechance.chat.service.model

import net.thechance.chat.api.dto.MessageRequestDto
import java.util.*


data class MessageRequestArgs(
    val messageId: UUID,
    val chatId: UUID,
    val senderId: UUID,
    val text: String?
)

fun MessageRequestDto.toRequestArgs(senderId: UUID): MessageRequestArgs {
    return MessageRequestArgs(
        messageId = messageId,
        chatId = this.chatId,
        senderId = senderId,
        text = this.text
    )
}