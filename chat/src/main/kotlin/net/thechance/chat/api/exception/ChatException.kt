package net.thechance.chat.api.exception


open class ChatException(
    val code: Int?= null,
    override val message: String,
) : Exception(message)

class NotFoundException(message: String) : ChatException(message = message)