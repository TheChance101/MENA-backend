package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.error.ErrorResponse
import net.thechance.wallet.exception.BlockedWalletUserException
import net.thechance.wallet.exception.NoTransactionsFoundException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["net.thechance.wallet"])
@Order(Ordered.HIGHEST_PRECEDENCE)
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

    @ExceptionHandler(BlockedWalletUserException::class)
    fun handleBlockedWalletUser(ex: BlockedWalletUserException): ResponseEntity<ErrorResponse> {
        val errorBody = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            message = ex.message ?: "Wallet user is blocked"
        )

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorBody)
    }
}