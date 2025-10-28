package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.DeleteChatResponse
import net.thechance.chat.api.dto.ErrorResponse
import net.thechance.chat.service.exception.DeleteChatException
import net.thechance.chat.service.exception.DeleteImagesFolderException
import net.thechance.chat.service.exception.ErrorCodes
import net.thechance.chat.service.exception.ImageUploadFailedException
import net.thechance.chat.service.exception.InvalidImageFormatException
import net.thechance.chat.service.exception.NotFoundException
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
            code = ErrorCodes.INVALID_IMAGE_FORMAT,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(ImageUploadFailedException:: class)
    fun handleImageUploadException(e: ImageUploadFailedException): ResponseEntity<ErrorResponse>{
        val error = ErrorResponse(
            code = ErrorCodes.IMAGE_UPLOAD_FAILED,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(DeleteImagesFolderException::class)
    fun handleDeleteImagesFolderException(e: DeleteImagesFolderException): ResponseEntity<DeleteChatResponse>{
        val error = DeleteChatResponse(
            message = e.message,
            success = false
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(DeleteChatException::class)
    fun handleDeleteChatException(e: DeleteChatException): ResponseEntity<DeleteChatResponse>{
        val error = DeleteChatResponse(
            message = e.message,
            success = false
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}