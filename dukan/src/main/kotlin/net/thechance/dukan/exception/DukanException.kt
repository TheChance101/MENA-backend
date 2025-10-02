package net.thechance.dukan.exception

import org.springframework.http.HttpStatus

open class DukanException(
    val code: Int,
    val status: HttpStatus,
    override val message: String,
) : Exception(message)
