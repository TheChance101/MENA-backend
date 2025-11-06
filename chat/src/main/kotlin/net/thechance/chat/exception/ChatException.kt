package net.thechance.chat.exception

import org.springframework.http.HttpStatus

open class ChatException(
    val code: String,
    val status: HttpStatus,
    override val message: String,
) : Exception(message)
