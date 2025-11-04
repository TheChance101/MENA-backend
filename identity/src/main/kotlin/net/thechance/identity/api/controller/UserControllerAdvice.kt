package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.UserNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [UserController::class])
@Order(1)
class UserControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(ProfileController::class.java)

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse("User not found")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }
}