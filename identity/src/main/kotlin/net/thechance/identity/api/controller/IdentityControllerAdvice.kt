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

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<ErrorResponse?> {
        logger.error("User not found: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("User not found"))
    }

    @ExceptionHandler(InvalidPhoneNumberException::class)
    fun handleInvalidPhoneNumberException(exception: InvalidPhoneNumberException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid phone number: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Invalid phone number"))
    }

    @ExceptionHandler(FrequentOtpRequestException::class)
    fun handleFrequentOtpRequestException(exception: FrequentOtpRequestException): ResponseEntity<ErrorResponse?> {
        logger.error("Frequent otp request: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ErrorResponse("Frequent otp requests, try again later"))
    }

    @ExceptionHandler(InvalidOtpException::class)
    fun handleInvalidOtpException(exception: InvalidOtpException): ResponseEntity<ErrorResponse?> {
        logger.error("OTP is invalid: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("OTP is invalid"))
    }

    @ExceptionHandler(OtpExpiredException::class)
    fun handleOtpExpiredException(exception: OtpExpiredException): ResponseEntity<ErrorResponse?> {
        logger.error("OTP is expired: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("OTP is expired"))
    }
}