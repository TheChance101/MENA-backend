package net.thechance.wallet.exception

import net.thechance.wallet.api.dto.error.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class WalletExceptionHandler {

    @ExceptionHandler(NoTransactionsFoundException::class)
    fun handleNoTransactionsFound(ex: NoTransactionsFoundException): ResponseEntity<ErrorResponse> {
        val errorBody = ErrorResponse(
            status = HttpStatus.NO_CONTENT.value(),
            message = ex.message ?: "No transactions found"
        )

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(errorBody)
    }
}