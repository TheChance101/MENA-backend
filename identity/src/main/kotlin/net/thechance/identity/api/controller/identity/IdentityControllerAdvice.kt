package net.thechance.identity.api.controller.identity

import net.thechance.identity.api.dto.error.ErrorResponse
import net.thechance.identity.exception.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [IdentityController::class])
@Order(1)
class IdentityControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(IdentityController::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = exception.bindingResult.fieldErrors.associate {
            it.field to it.defaultMessage
        }
        logger.error("Validation failed: $errors", exception)
        val errorResponse = ErrorResponse("Data not valid")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(UserIpIsBlockedException::class)
    fun handleUserIpIsBlockedException(exception: UserIpIsBlockedException): ResponseEntity<ErrorResponse> {
        logger.error("User ip is blocked: ${exception.message}", exception)
        val errorResponse = ErrorResponse("User ip is blocked")
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(errorResponse)
    }

    @ExceptionHandler(UserIsBlockedException::class)
    fun handleUserIsBlockedException(exception: UserIsBlockedException): ResponseEntity<ErrorResponse> {
        logger.error("User is blocked: ${exception.message}", exception)
        val errorResponse = ErrorResponse("User is blocked")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse)
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(exception: InvalidCredentialsException): ResponseEntity<ErrorResponse> {
        logger.error("Invalid credentials: ${exception.message}", exception)
        val errorResponse = ErrorResponse("Invalid credentials")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }

    @ExceptionHandler(InvalidIpException::class)
    fun handleInvalidIpException(exception: InvalidIpException): ResponseEntity<ErrorResponse> {
        logger.error("Invalid IP: ${exception.message}", exception)
        val errorResponse = ErrorResponse("Invalid IP")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshTokenException(exception: InvalidRefreshTokenException): ResponseEntity<ErrorResponse> {
        logger.error("Invalid refresh token: ${exception.message}", exception)
        val errorResponse = ErrorResponse("Invalid refresh token")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse("User not found")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }

    @ExceptionHandler(InvalidPhoneNumberException::class)
    fun handleInvalidPhoneNumberException(exception: InvalidPhoneNumberException): ResponseEntity<ErrorResponse> {
        logger.error("Invalid phone number: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "Invalid phone number")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(FrequentOtpRequestException::class)
    fun handleFrequentOtpRequestException(exception: FrequentOtpRequestException): ResponseEntity<ErrorResponse> {
        logger.error("Frequent otp request: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "Frequent otp requests, try again later")
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(errorResponse)
    }

    @ExceptionHandler(InvalidOtpException::class)
    fun handleInvalidOtpException(exception: InvalidOtpException): ResponseEntity<ErrorResponse> {
        logger.error("OTP is invalid: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "OTP is invalid")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse)
    }

    @ExceptionHandler(OtpExpiredException::class)
    fun handleOtpExpiredException(exception: OtpExpiredException): ResponseEntity<ErrorResponse> {
        logger.error("OTP is expired: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "OTP is expired")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(PasswordMismatchException::class)
    fun handlePasswordMismatchException(exception: PasswordMismatchException): ResponseEntity<ErrorResponse> {
        logger.error("Password mismatch: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "Password and Confirm Password do not match")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(PasswordNotUpdatedException::class)
    fun handlePasswordNotUpdatedException(exception: PasswordNotUpdatedException): ResponseEntity<ErrorResponse> {
        logger.error("Password not updated: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "Password not updated")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(exception: UnauthorizedException): ResponseEntity<ErrorResponse> {
        logger.error("Unauthorized: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "Unauthorized")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(exception: UserAlreadyExistsException): ResponseEntity<ErrorResponse> {
        logger.error("User Already Exists: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "User Already Exists")
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(errorResponse)
    }

    @ExceptionHandler(InvalidImageException::class)
    fun handleInvalidImageException(exception: InvalidImageException): ResponseEntity<ErrorResponse> {
        logger.error("Invalid image: ${exception.message}", exception)
        val errorResponse = ErrorResponse("Image extension ${exception.extension} is not supported")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(UnknownErrorException::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        logger.error(exception.message, exception)
        val errorResponse = ErrorResponse("Internal server error")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }
}