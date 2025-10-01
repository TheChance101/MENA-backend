package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.InvalidImageException
import net.thechance.identity.exception.UnknownErrorException
import net.thechance.identity.exception.UserNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [ProfileController::class])
@Order(1)
class ProfileControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(IdentityController::class.java)

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
            .body(ErrorResponse("Image extesion ${exception.extension} is not supported"))
    }

    @ExceptionHandler(UnknownErrorException::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message, exception)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("Internal server error"))
    }
}