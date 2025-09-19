package net.thechance.trends.api.controller

import net.thechance.trends.api.controller.exception.ReelNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["net.thechance.trends.api.controller"])
class TrendsGlobalExceptionHandler {

    @ExceptionHandler(ReelNotFoundException::class)
    fun handleReelNotFoundException(exception: ReelNotFoundException): ResponseEntity<String> {
        val message = exception.localizedMessage

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message)
    }
}