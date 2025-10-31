package net.thechance.identity.api.controller.register

import net.thechance.identity.api.dto.ErrorResponse
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

@RestControllerAdvice(assignableTypes = [RegisterController::class])
@Order(1)
class RegisterControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(RegisterController::class.java)

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

    @ExceptionHandler(InvalidPhoneNumberException::class)
    fun handleInvalidPhoneNumberException(exception: InvalidPhoneNumberException): ResponseEntity<ErrorResponse> {
        logger.error("Invalid phone number: ${exception.message}", exception)
        val errorResponse = ErrorResponse(exception.message ?: "Invalid phone number")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
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