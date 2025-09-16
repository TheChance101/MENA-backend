package identity.config.exceptionHandling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler

class IdentityExceptionHandler {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED.value(),
            exception = e.javaClass.name,
            error = "Invalid phone number or password",
            message = e.message,
        )
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }
}