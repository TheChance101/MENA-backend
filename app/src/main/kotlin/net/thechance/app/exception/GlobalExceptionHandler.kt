package net.thechance.app.exception

import net.thechance.app.exception.exceptions.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND.value(), "Resource Not Found")
    }

    @ExceptionHandler(ResourceConflictException::class)
    fun handleResourceConflict(ex: ResourceConflictException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.CONFLICT.value(), "Resource Conflict")
    }

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
    fun handleGeneral(): ResponseEntity<ApiErrorResponse> {
        val apiError = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "An unexpected error occurred while processing your request"
        )
        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
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