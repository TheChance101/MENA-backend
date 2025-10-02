package net.thechance.dukan.exception

import org.springframework.http.HttpStatus

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorCode(
    val code: Int,
    val status: HttpStatus
)