package net.thechance.dukan.exception

import net.thechance.dukan.api.dto.ErrorResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Suppress("NO_REFLECTION_IN_CLASS_PATH")
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
        val errorCodeAnnotation = ex::class.annotations.filterIsInstance<ErrorCode>().firstOrNull()

        val code = errorCodeAnnotation?.code ?: 500
        val status = errorCodeAnnotation?.status ?: HttpStatus.INTERNAL_SERVER_ERROR

        val response = ErrorResponse(
            message = ex.message ?: "server error",
            errorCode = code
        )
        return ResponseEntity(response, status)
    }


}