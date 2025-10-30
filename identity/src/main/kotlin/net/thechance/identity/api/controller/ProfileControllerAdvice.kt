package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.InvalidImageException
import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.UnauthorizedException
import net.thechance.identity.exception.UnknownErrorException
import net.thechance.identity.exception.UserNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [ProfileController::class])
@Order(1)
class ProfileControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(IdentityController::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse?> {
        val firstException = exception.getFirstException()
        logger.error("Validation failed: $firstException", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(firstException))
    }

    private fun MethodArgumentNotValidException.getFirstException(): String =
        bindingResult.fieldErrors.first().let {
            it.defaultMessage ?: "Error in field: $it.field"
        }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("User not found"))
    }

    @ExceptionHandler(InvalidImageException::class)
    fun handleInvalidImageException(exception: InvalidImageException): ResponseEntity<ErrorResponse?> {
        logger.error("Invalid image: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Image extension ${exception.extension} is not supported"))
    }

    @ExceptionHandler(UnknownErrorException::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message, exception)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("Internal server error"))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(exception: UnauthorizedException): ResponseEntity<ErrorResponse?> {
        logger.error("Unauthorized: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(exception.message ?: "Unauthorized"))
    }

    @ExceptionHandler(PasswordMismatchException::class)
    fun handlePasswordMismatchException(exception: PasswordMismatchException): ResponseEntity<ErrorResponse?> {
        logger.error("Password mismatch: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(exception.message ?: "Password and Confirm Password do not match"))
    }
}