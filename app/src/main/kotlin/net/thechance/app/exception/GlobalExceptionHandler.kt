package net.thechance.app.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val errors = ex.bindingResult.allErrors.map {
            when (it) {
                is FieldError -> "${it.field}: ${it.defaultMessage}"
                else -> it.defaultMessage ?: "Validation error"
            }
        }
        return createErrorResponse(
            ex = ex,
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed: ${errors.joinToString(", ")}"
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(ex: MissingServletRequestParameterException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(
            ex = ex,
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Missing required parameter: ${ex.parameterName}"
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(
            ex = ex, status = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Unexpected Error"
        )
    }

    private fun createErrorResponse(
        ex: Exception, status: Int, message: String
    ): ResponseEntity<ApiErrorResponse> {
        val apiError = ApiErrorResponse(
            status = status, message = ex.message ?: ""
        )
        logger.error("{}: {}", message, ex.message)
        return ResponseEntity(apiError, HttpStatus.valueOf(status))
    }
}