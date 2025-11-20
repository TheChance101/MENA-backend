package net.thechance.chat.service.model

import org.springframework.web.multipart.MultipartFile
import java.util.*

data class MessageAudioRequestArgs(
    val messageId: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val audio: MultipartFile,
    val audioDurationMs: Long,
)