package identity.config.exceptionHandling

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler

class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED.value(),
            exception = e.javaClass.name,
            error = "Invalid phone number or password",
            message = e.message,
        )
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshTokenException(ex: InvalidRefreshTokenException): ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = ErrorCode.INVALID_REFRESH_TOKEN,
            exception = ex.javaClass.name,
            error = "Invalid refresh token",
            message = ex.message ?: "Invalid refresh token"
        )
        return ResponseEntity(apiError, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllOtherExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        logger.error("Request failed: ${request.method} ${request.requestURI} | Error: ${ex.message}", ex)

        val apiError = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            exception = ex.javaClass.name,
            error = "Internal Server Error",
            message = ex.message ?: "Unexpected error occurred"
        )
        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}