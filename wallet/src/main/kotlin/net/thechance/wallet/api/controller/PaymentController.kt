package net.thechance.wallet.api.controller

import net.thechance.wallet.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@RestController
@RequestMapping("/wallet/payment")
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/submit")
    fun submitTransaction(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam transactionId: UUID,
    ): ResponseEntity<Unit> {
        paymentService.pay(userId, transactionId)
        return ResponseEntity.ok().build()
    }
}