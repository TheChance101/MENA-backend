package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.transaction.TransactionFilterRequest
import net.thechance.wallet.api.dto.transaction.TransactionPageResponse
import net.thechance.wallet.api.dto.transaction.TransactionType
import net.thechance.wallet.api.dto.transaction.toResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.TransactionService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*


@RestController
@RequestMapping("/wallet")
class TransactionController(
    private val transactionService: TransactionService
) {
    @GetMapping("/transactions")
    fun getFilteredTransactions(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(required = false) type: TransactionType?,
        @RequestParam(required = false) status: Transaction.Status?,
        @RequestParam(required = false) startDate: LocalDateTime?,
        @RequestParam(required = false) endDate: LocalDateTime?,
        pageable: Pageable
    ): ResponseEntity<TransactionPageResponse> {
        val filter = TransactionFilterRequest(
            type = type,
            status = status,
            startDate = startDate,
            endDate = endDate
        )
        val transactions =
            transactionService.getFilteredTransactions(filter = filter, pageable = pageable, currentUserId = userId)
                .map { it.toResponse() }

        val earliestDate = transactionService.getUserFirstTransactionDate(currentUserId = userId)

        val response = TransactionPageResponse(
            transactions = transactions,
            earliestTransactionDate = earliestDate
        )

        return ResponseEntity.ok(response)
    }
}