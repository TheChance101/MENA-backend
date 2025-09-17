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

    @ExceptionHandler(RefreshTokenExpiredException::class)
    fun handleRefreshTokenExpired(ex: RefreshTokenExpiredException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, ErrorCodes.INVALID_REFRESH_TOKEN, "Refresh Token Expired")
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, ErrorCodes.INVALID_CREDENTIALS, "Invalid Credentials")
    }

    @ExceptionHandler(JwtTokenExpiredException::class)
    fun handleJwtTokenExpired(ex: JwtTokenExpiredException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, ErrorCodes.JWT_EXPIRED, "JWT Token Expired")
    }

    @ExceptionHandler(AuthenticationRequiredException::class)
    fun handleAuthenticationRequired(ex: AuthenticationRequiredException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.UNAUTHORIZED.value(), "Authentication Required")
    }

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
        val errors = ex.validationErrors ?: emptyList()
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors)
    }

    @ExceptionHandler(MissingRequiredFieldException::class)
    fun handleMissingRequiredField(ex: MissingRequiredFieldException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST.value(), "Missing Required Field")
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiErrorResponse> {
        val apiError = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            exception = ex.javaClass.simpleName,
            error = "Internal Server Error",
            message = "An unexpected error occurred while processing your request"
        )
        logger.error("Unexpected error: {}", ex.message, ex)
        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun createErrorResponse(
        ex: Exception, status: Int, error: String, errors: List<String>? = null
    ): ResponseEntity<ApiErrorResponse> {
        val apiError = ApiErrorResponse(
            status = status, exception = ex.javaClass.simpleName, error = error, message = ex.message, errors = errors
        )
        logger.warn("{}: {}", error, ex.message)
        return ResponseEntity(apiError, HttpStatus.valueOf(status))
    }
}