package net.thechance.chat.service.exception


open class ChatException(
    override val message: String,
) : Exception(message)

class NotFoundException(message: String) : ChatException(message = message)