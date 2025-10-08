package net.thechance.faith.api.controller.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["net.thechance.faith.api.controller"])
class FaithExceptionHandler {

    @ExceptionHandler(AyahBookmarkNotFoundException::class)
    fun onBookmarkNotFoundError(exception: AyahBookmarkNotFoundException): ResponseEntity<ApiErrorResponse> =
        createErrorResponse(
            exception = exception,
            status = HttpStatus.NOT_FOUND.value(),
        )

    @ExceptionHandler(CannotGetPrayerTimesException::class)
    fun onCannotGetPrayerTimesError(exception: CannotGetPrayerTimesException): ResponseEntity<ApiErrorResponse> =
        createErrorResponse(
            exception = exception,
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
        )

    private fun createErrorResponse(
        exception: Exception, status: Int
    ): ResponseEntity<ApiErrorResponse> {
        val apiError = ApiErrorResponse(
            status = status, message = exception.message ?: ""
        )
        return ResponseEntity(apiError, HttpStatus.valueOf(status))
    }
}
