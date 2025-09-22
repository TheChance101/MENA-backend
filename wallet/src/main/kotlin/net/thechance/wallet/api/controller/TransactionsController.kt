package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.TransactionHistoryResponse
import net.thechance.wallet.service.WalletService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/transactions")
class TransactionsController(
    private val walletService: WalletService
) {

    @GetMapping("/history")
    fun getTransactionHistory(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<Page<TransactionHistoryResponse>> {
        val historyPage = walletService.getTransactionHistory(userId, pageable)
        return ResponseEntity.ok(historyPage)
    }
}