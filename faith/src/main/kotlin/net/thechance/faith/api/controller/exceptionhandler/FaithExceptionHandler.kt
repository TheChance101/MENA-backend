package net.thechance.faith.api.controller.exceptionhandler

import net.thechance.faith.api.dto.error.ApiErrorResponse
import net.thechance.faith.exception.AyahBookmarkNotFoundException
import net.thechance.faith.exception.FailedToGetPrayerTimesException
import net.thechance.faith.exception.InvalidDateFormatException
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
            status = HttpStatus.NOT_FOUND,
        )
    }

    @ExceptionHandler(FailedToGetPrayerTimesException::class)
    fun onCannotGetPrayerTimesError(exception: FailedToGetPrayerTimesException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(
            message = exception.message ?: "failed to get prayer times",
            exception = exception,
            status = HttpStatus.SERVICE_UNAVAILABLE,
        )
    }

    @ExceptionHandler(InvalidDateFormatException::class)
    fun onInvalidDateFormatError(exception: InvalidDateFormatException): ResponseEntity<ApiErrorResponse> {
        return createErrorResponse(
            message = "Invalid date format.",
            exception = exception,
            status = HttpStatus.BAD_REQUEST,
        )
    }

    private fun createErrorResponse(
        message: String,
        exception: Exception,
        status: HttpStatus
    ): ResponseEntity<ApiErrorResponse> {
        logger.error(message, exception)
        val apiError = ApiErrorResponse(status = status.value(), message = message)
        return ResponseEntity(apiError, status)
    }
}
