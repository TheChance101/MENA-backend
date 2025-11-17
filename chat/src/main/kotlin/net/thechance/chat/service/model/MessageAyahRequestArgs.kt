package net.thechance.chat.service.model

import org.springframework.web.multipart.MultipartFile
import java.util.UUID

data class MessageAyahRequestArgs(
    val messageId: UUID,
    val chatId: UUID,
    val senderId: UUID,
    val ayahNumber: Int,
    val suraNumber: Int,
    val ayahText: String
)
