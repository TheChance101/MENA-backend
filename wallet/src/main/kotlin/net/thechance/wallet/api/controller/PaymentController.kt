package net.thechance.wallet.api.controller

import net.thechance.wallet.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/wallet/payments")
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