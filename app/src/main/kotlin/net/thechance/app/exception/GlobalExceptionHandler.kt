package net.thechance.app.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AccessForbiddenException::class)
    fun handleAccessForbidden(ex: AccessForbiddenException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.FORBIDDEN.value(), "Access Forbidden")
    }

    @ExceptionHandler(RequestValidationException::class)
    fun handleRequestValidation(ex: RequestValidationException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST.value(), "Validation Failed")
    }

    @ExceptionHandler(MissingRequiredFieldException::class)
    fun handleMissingRequiredField(ex: MissingRequiredFieldException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST.value(), "Missing Required Field")
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected Error")
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