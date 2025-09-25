package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [IdentityController::class])
@Order(1)
class IdentityControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(IdentityController::class.java)

    @ExceptionHandler(UserIsBlockedException::class)
    fun handleUserIsBlockedException(exception: UserIsBlockedException): ResponseEntity<ErrorResponse?> {
        logger.error("User is blocked: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse("User is blocked"))
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(exception: InvalidCredentialsException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid credentials: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("Invalid credentials"))
    }

    @ExceptionHandler(InvalidIpException::class)
    fun handleInvalidIpException(exception: InvalidIpException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid IP: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Invalid IP"))
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshTokenException(exception: InvalidRefreshTokenException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid refresh token: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("Invalid refresh token"))
    }

    @ExceptionHandler(PasswordMismatchException::class)
    fun handlePasswordMismatchException(exception: PasswordMismatchException): ResponseEntity<ErrorResponse?> {
        logger.error("Password mismatch: ${exception.message}", exception)
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse(exception.message.toString()))
    }

    @ExceptionHandler(PasswordNotUpdatedException::class)
    fun handlePasswordNotUpdatedException(exception: PasswordNotUpdatedException): ResponseEntity<ErrorResponse?> {
        logger.error("Password not updated: ${exception.message}", exception)
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse(exception.message.toString()))
    }
}