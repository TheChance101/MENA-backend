package net.thechance.faith.api.controller.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onValidationError(exception: MethodArgumentNotValidException): BaseErrorResponse {
        val errors = exception.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }
        return BaseErrorResponse(
            message = "Validation failed",
            errors = errors
        )
    }
}