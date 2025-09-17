package net.thechance.trends.api.controller.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(exception: NoSuchElementException): BaseErrorResponse {
        val message = exception.message ?: "Resource not found!"

        return BaseErrorResponse(
            message = message,
            errors = null
        )
    }

    @ExceptionHandler(IllegalAccessException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleIllegalException(e: IllegalAccessException): BaseErrorResponse {
        val message = e.message ?: "You can't access this resource!"

        return BaseErrorResponse(
            message = message,
            errors = null
        )
    }
}