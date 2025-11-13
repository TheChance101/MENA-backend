package net.thechance.chat.service.model

import net.thechance.chat.api.dto.TextMessageRequestDto
import java.util.*


data class MessageRequestArgs(
    val messageId: UUID,
    val chatId: UUID,
    val senderId: UUID,
    val text: String
)

fun TextMessageRequestDto.toRequestArgs(senderId: UUID): MessageRequestArgs {
    return MessageRequestArgs(
        messageId = messageId,
        chatId = this.chatId,
        senderId = senderId,
        text = this.text
    )
}