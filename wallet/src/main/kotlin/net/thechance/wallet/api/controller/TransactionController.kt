package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.transaction.TransactionPageResponse
import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.api.dto.transaction.toResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.helper.TransactionFilterParams
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@RestController
@RequestMapping("/wallet/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {
    @GetMapping
    fun getFilteredTransactions(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(required = false) type: UserTransactionType?,
        @RequestParam(required = false) status: Transaction.Status?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        pageable: Pageable
    ): ResponseEntity<TransactionPageResponse> {

        val transactions =
            transactionService.getFilteredTransactions(
                TransactionFilterParams(
                    type = type,
                    status = status,
                    startDate = startDate,
                    endDate = endDate
                ),
                pageable = pageable,
                currentUserId = userId
            )
                .map { it.toResponse(currentUserId = userId) }


        val response = TransactionPageResponse(
            transactions = transactions.content,
            page = transactions.number,
            pageSize = transactions.size,
            totalElements = transactions.totalElements,
            totalPages = transactions.totalPages
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/first")
    fun getUserFirstTransactionDate(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<LocalDateTime?> {

        val firstTransactionDate = transactionService.getUserFirstTransactionDate(currentUserId = userId)
        return ResponseEntity.ok(firstTransactionDate)
    }
}

