package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.ErrorResponse
import net.thechance.chat.service.exception.*
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

    @ExceptionHandler(InvalidImageFormatException::class)
    fun handleInvalidImageFormatException(e: InvalidImageFormatException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ErrorCodes.INVALID_IMAGE_FORMAT,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(ImageUploadFailedException::class)
    fun handleImageUploadException(e: ImageUploadFailedException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ErrorCodes.IMAGE_UPLOAD_FAILED,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(InvalidAudioFormatException::class)
    fun handleInvalidAudioFormatException(e: InvalidAudioFormatException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ErrorCodes.INVALID_AUDIO_FORMAT,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(AudioUploadFailedException::class)
    fun handleAudioUploadException(e: AudioUploadFailedException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ErrorCodes.AUDIO_UPLOAD_FAILED,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(InvalidTimeFormatException::class)
    fun handleInvalidTimeFormatException(e: InvalidTimeFormatException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ErrorCodes.INVALID_TIME_FORMAT,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(MalformedMessageContentException::class)
    fun handleMalformedMessageContentException(e: MalformedMessageContentException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ErrorCodes.MALFORMED_MESSAGE_CONTENT,
            message = e.message
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error)
    }
}