package net.thechance.trends.api.controller

import net.thechance.trends.api.controller.exception.ReelNotFoundException
import net.thechance.trends.api.dto.BaseErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ReelNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(exception: ReelNotFoundException): BaseErrorResponse {
        val message = exception.localizedMessage

        return BaseErrorResponse(
            message = message,
            errors = null
        )
    }

    @ExceptionHandler(IllegalAccessException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleIllegalException(e: IllegalAccessException): BaseErrorResponse {
        val message = e.localizedMessage

        return BaseErrorResponse(
            message = message,
            errors = null
        )
    }
}