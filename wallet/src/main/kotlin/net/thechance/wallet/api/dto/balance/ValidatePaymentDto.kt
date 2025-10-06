package net.thechance.wallet.api.dto.balance

import net.thechance.wallet.service.helper.PaymentAmountValidationResult
import java.util.UUID

data class PaymentAmountValidationResponse(
    val isValid: Boolean,
    val currentBalance: Double,
    val recipientName: String,
    val recipientImageUrl: String?
)

data class PaymentAmountValidationRequest(
    val amount: Double,
    val recipientId: UUID
)

fun PaymentAmountValidationResult.toResponse(): PaymentAmountValidationResponse =
    PaymentAmountValidationResponse(
        isValid = isValid,
        currentBalance = currentBalance,
        recipientName = recipientName,
        recipientImageUrl = recipientImageUrl
    )