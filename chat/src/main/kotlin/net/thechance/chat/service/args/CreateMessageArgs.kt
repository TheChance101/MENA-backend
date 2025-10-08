package net.thechance.chat.service.args

import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*

data class CreateMessageArgs(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String?,
    val attachments: List<MultipartFile>?,
    val sendAt: Instant,
    val isRead: Boolean
)