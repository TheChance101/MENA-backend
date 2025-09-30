package net.thechance.dukan.exception

import net.thechance.dukan.api.dto.ErrorResponse
import org.hibernate.validator.internal.util.logging.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["net.thechance.dukan.api.controller"])
@Order(1)
class DukanExceptionHandler {

    @ExceptionHandler(Throwable::class)
    fun handleServerErrors(ex: Throwable): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = "Internal server error",
            errorCode = 500
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate { fieldError ->
            val key = fieldError.field
            key to (fieldError.defaultMessage ?: "validation invalid field")
        }
        val response = ErrorResponse(
            message = "validation failed",
            errors = errors
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception): ResponseEntity<ErrorResponse> {
        val messageKey = ex.message ?: "server error"
        val (code, status) = ExceptionErrorCodes.businessErrorMap[ex::class]
            ?: (500 to HttpStatus.INTERNAL_SERVER_ERROR)

        val response = ErrorResponse(
            message = ex.message ?: messageKey,
            errorCode = code
        )
        return ResponseEntity(response, status)
    }

}