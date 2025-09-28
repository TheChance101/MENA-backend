package net.thechance.wallet.api.controller

import jakarta.servlet.http.HttpServletResponse
import net.thechance.wallet.api.dto.transaction.FirstTransactionDateResponse
import net.thechance.wallet.api.dto.transaction.TransactionPageResponse
import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.api.dto.transaction.toResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.StatementService
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
import java.util.*


@RestController
@RequestMapping("/wallet/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val statementService: StatementService
) {
    @GetMapping
    fun getFilteredTransactions(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(required = false) types: List<UserTransactionType> = emptyList(),
        @RequestParam(required = false) status: Transaction.Status?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        pageable: Pageable
    ): ResponseEntity<TransactionPageResponse> {

        val transactions =
            transactionService.getFilteredTransactions(
                TransactionFilterParams(
                    types = types,
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

    @GetMapping("/first-date")
    fun getUserFirstTransactionDate(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<FirstTransactionDateResponse> {

        val date = transactionService.getUserFirstTransactionDate(currentUserId = userId)?.toLocalDate()
        return ResponseEntity.ok(FirstTransactionDateResponse(firstTransactionDate = date))
    }

    @GetMapping("/statement")
    fun generateStatement(
        response: HttpServletResponse,
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(required = false) types: List<UserTransactionType> = emptyList(),
        @RequestParam(required = false) status: Transaction.Status?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
    ) {
        statementService.generateStatementPdf(
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            types = types,
            status = status,
            response = response
        )
    }
}

