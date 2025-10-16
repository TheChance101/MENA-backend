package net.thechance.chat.service.model

import org.springframework.web.multipart.MultipartFile
import java.util.*

data class MessageImageRequestArgs(
    val chatId: UUID,
    val senderId: UUID,
    val image: MultipartFile,
    val messageId: UUID?
)
