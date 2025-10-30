package net.thechance.faith.api.controller.exception

import org.springframework.http.HttpStatus

open class FaithException(
    val code: Int,
    val status: HttpStatus,
    override val message: String,
) : Exception(message)