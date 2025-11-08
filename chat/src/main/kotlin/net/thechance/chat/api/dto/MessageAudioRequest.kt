package net.thechance.chat.api.dto

import net.thechance.chat.service.model.MessageAudioRequestArgs
import org.springframework.web.multipart.MultipartFile
import java.util.*

data class MessageAudioRequest(
    val chatId: UUID,
    val audio: MultipartFile,
    val audioDurationMs: Long,
)

fun MessageAudioRequest.toRequestArgs(senderId: UUID) =
    MessageAudioRequestArgs(
        senderId = senderId,
        chatId = chatId,
        audio = audio,
        audioDurationMs = audioDurationMs,
    )
