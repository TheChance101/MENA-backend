package net.thechance.trends.exception

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


    @ExceptionHandler(TrendUserNotFoundException::class)
    fun handleTrendUserNotFoundException(exception: TrendUserNotFoundException): ResponseEntity<String> {
        val message = exception.localizedMessage

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message)
    }

    @ExceptionHandler(TrendCategoryNotFoundException::class)
    fun handleTrendCategoryNotFoundException(exception: TrendCategoryNotFoundException): ResponseEntity<String> {
        val message = exception.localizedMessage

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message)
    }

    @ExceptionHandler(InvalidTrendInputException::class)
    fun handleInvalidTrendInputException(exception: InvalidTrendInputException): ResponseEntity<String> {
        val message = exception.localizedMessage

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message)
    }

    @ExceptionHandler(InvalidVideoException::class)
    fun handleInvalidVideoException(exception: InvalidVideoException): ResponseEntity<String> {
        val message = exception.localizedMessage

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message)
    }

    @ExceptionHandler(VideoUploadFailedException::class)
    fun handleVideoUploadFailedException(exception: VideoUploadFailedException): ResponseEntity<String> {
        val message = exception.localizedMessage

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message)
    }
}