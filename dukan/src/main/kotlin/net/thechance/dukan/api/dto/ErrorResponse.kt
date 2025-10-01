package net.thechance.dukan.api.dto

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val message: String,
    val errors: Map<String, String>? = null,
    val errorCode: Int? = null
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorCode(
    val code: Int,
    val status: HttpStatus
)
