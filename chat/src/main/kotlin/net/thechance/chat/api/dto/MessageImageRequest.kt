package net.thechance.chat.api.dto

import net.thechance.chat.service.model.MessageImageRequestArgs
import org.springframework.web.multipart.MultipartFile
import java.util.*

data class MessageImageRequest(
    val chatId: UUID,
    val image: MultipartFile,
    val messageId: UUID?
)

fun MessageImageRequest.toRequestArgs(senderId: UUID): MessageImageRequestArgs {
    return MessageImageRequestArgs(
        chatId = this.chatId,
        senderId = senderId,
        image = this.image,
        messageId = this.messageId
    )
}