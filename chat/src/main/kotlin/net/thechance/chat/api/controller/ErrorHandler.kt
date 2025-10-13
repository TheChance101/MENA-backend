package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.ErrorResponse
import net.thechance.chat.service.exception.NotFoundException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(1)
class ChatControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(e.message ?: "Resource not found")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error)
    }
}