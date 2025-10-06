package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.balance.BalanceResponse
import net.thechance.wallet.api.dto.balance.PaymentAmountValidationRequest
import net.thechance.wallet.api.dto.balance.PaymentAmountValidationResponse
import net.thechance.wallet.api.dto.balance.toResponse
import net.thechance.wallet.service.WalletService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@RestController
@RequestMapping("/wallet")
class BalanceController(
    private val walletService: WalletService
) {

    @GetMapping("/balance")
    fun getUserBalance(@AuthenticationPrincipal userId: UUID): ResponseEntity<BalanceResponse> {

        val balance = walletService.getUserBalance(userId)

        val response = BalanceResponse(balance = balance)

        return ResponseEntity.ok(response)
    }

    @PostMapping("/validate-payment")
    fun validatePayment(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody paymentAmountValidationRequest: PaymentAmountValidationRequest
    ): ResponseEntity<PaymentAmountValidationResponse> {

        val validationResult = walletService.validatePaymentAmount(
            userId = userId,
            amount = paymentAmountValidationRequest.amount,
            receiverId = paymentAmountValidationRequest.receiverId
        )

        return ResponseEntity.ok(validationResult.toResponse())
    }
}