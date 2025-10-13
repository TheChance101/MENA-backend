package net.thechance.chat.api.dto

import org.springframework.web.multipart.MultipartFile
import java.util.*

data class MessageImagesRequestDto(
    val chatId: UUID,
    val images: List<MultipartFile>
)


data class MessageImagesRequestArgs(
    val chatId: UUID,
    val senderId: UUID,
    val images: List<MultipartFile>
)


fun MessageImagesRequestDto.toReqArgs(senderId: UUID): MessageImagesRequestArgs {
    return MessageImagesRequestArgs(
        chatId = this.chatId,
        senderId = senderId,
        images = this.images
    )
}