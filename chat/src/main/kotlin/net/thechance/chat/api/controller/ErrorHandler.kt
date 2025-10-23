package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.ErrorResponse
import net.thechance.chat.api.exception.ImageUploadFailedException
import net.thechance.chat.api.exception.InvalidImageFormatException
import net.thechance.chat.api.exception.NotFoundException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(1)
class ChatControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(e.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error)
    }

    @ExceptionHandler(InvalidImageFormatException:: class)
    fun handleInvalidImageFormatException(e: InvalidImageFormatException): ResponseEntity<ErrorResponse>{
        val error = ErrorResponse(
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(ImageUploadFailedException:: class)
    fun handleImageUploadException(e: ImageUploadFailedException): ResponseEntity<ErrorResponse>{
        val error = ErrorResponse(
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}