package net.thechance.faith.api.controller.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["net.thechance.faith.api.controller"])
class FaithExceptionHandler {

    private val logger = LoggerFactory.getLogger(FaithExceptionHandler::class.java)

    @ExceptionHandler(AyahBookmarkNotFoundException::class)
    fun onBookmarkNotFoundError(exception: AyahBookmarkNotFoundException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(
            message = "Ayah bookmark not found",
            exception = exception,
            status = HttpStatus.NOT_FOUND.value(),
        )
    }

    @ExceptionHandler(CannotGetPrayerTimesException::class)
    fun onCannotGetPrayerTimesError(exception: CannotGetPrayerTimesException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(
            message = "Cannot get prayer times",
            exception = exception,
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
        )
    }

    private fun createErrorResponse(
        message: String,
        exception: Exception,
        status: Int
    ): ResponseEntity<ApiErrorResponse> {
        logger.error("{}: {}", message, exception.message)
        val apiError = ApiErrorResponse(
            status = status, message = message
        )
        return ResponseEntity(apiError, HttpStatus.valueOf(status))
    }
}
