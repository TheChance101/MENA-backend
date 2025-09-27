package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.TransactionDetailsResponse
import net.thechance.wallet.service.WalletService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/wallet/transactions")
class TransactionsController(
    private val walletService: WalletService
) {
    @GetMapping("/{transactionId}")
    fun getTransactionDetails(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable transactionId: UUID
    ): ResponseEntity<TransactionDetailsResponse> {
        val transactionDetails = walletService.getTransactionDetails(transactionId, userId)
        return ResponseEntity.ok(transactionDetails)
    }
}