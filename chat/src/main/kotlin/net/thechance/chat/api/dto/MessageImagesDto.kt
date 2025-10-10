package net.thechance.chat.api.dto

import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*

data class MessageImagesRequestDto(
    val chatId: UUID,
    val images: List<MultipartFile>
)

fun MessageImagesRequestDto.toMessageArgs(senderId: UUID): CreateMessageArgs {
    return CreateMessageArgs(
        id = UUID.randomUUID(),
        senderId = senderId,
        chatId = this.chatId,
        text = null,
        sendAt = Instant.now(),
        isRead = false
    )
}

