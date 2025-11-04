package net.thechance.identity.api.controller.adminAuth

import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.InvalidRefreshTokenException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [AdminAuthController::class])
@Order(1)
class AdminControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(AdminAuthController::class.java)

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(exception: InvalidCredentialsException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid credentials: ${exception.message}", exception)
        return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("Invalid credentials"))
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshTokenException(exception: InvalidRefreshTokenException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid refresh token: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("Invalid refresh token"))
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(exception: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Admin auth error: ${exception.message}", exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse("Internal server error"))
    }
}

