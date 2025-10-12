package net.thechance.chat.api.dto

import org.springframework.web.multipart.MultipartFile
import java.util.*

data class MessageImagesRequestDto(
    val chatId: UUID,
    val images: List<MultipartFile>
)

