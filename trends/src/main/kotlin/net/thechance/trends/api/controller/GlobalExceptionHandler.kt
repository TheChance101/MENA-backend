package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.BaseErrorResponse
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


    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: IllegalArgumentException): BaseErrorResponse {
        val message = e.message ?: "Something went wrong!"
        return BaseErrorResponse(
            message = message,
            errors = null
        )
    }
}