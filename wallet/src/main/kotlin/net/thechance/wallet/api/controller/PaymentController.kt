package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.balance.PaymentAmountValidationRequest
import net.thechance.wallet.api.dto.balance.PaymentAmountValidationResponse
import net.thechance.wallet.api.dto.balance.toResponse
import net.thechance.wallet.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@RestController
@RequestMapping("/wallet/payment")
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/isValid")
    fun validatePayment(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody paymentAmountValidationRequest: PaymentAmountValidationRequest
    ): ResponseEntity<PaymentAmountValidationResponse> {

        val validationResult = paymentService.validatePaymentAmount(
            userId = userId,
            amount = paymentAmountValidationRequest.amount,
            receiverId = paymentAmountValidationRequest.receiverId
        )

        return ResponseEntity.ok(validationResult.toResponse())
    }
}