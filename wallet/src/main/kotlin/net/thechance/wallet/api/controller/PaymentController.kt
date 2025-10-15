package net.thechance.wallet.api.controller

import net.thechance.wallet.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/wallet/payment")
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/{transactionId}/submit")
    fun submitTransaction(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable transactionId: UUID,
    ): ResponseEntity<Unit> {
        paymentService.pay(userId, transactionId)
        return ResponseEntity.ok().build()
    }
}