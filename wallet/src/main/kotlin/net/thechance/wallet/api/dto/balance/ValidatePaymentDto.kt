package net.thechance.wallet.api.dto.balance

import net.thechance.wallet.service.helper.PaymentAmountValidationResult
import java.util.UUID

data class PaymentAmountValidationResponse(
    val isValid: Boolean,
    val currentBalance: Double,
    val receiverName: String,
    val receiverImageUrl: String?
)

data class PaymentAmountValidationRequest(
    val amount: Double,
    val receiverId: UUID
)

fun PaymentAmountValidationResult.toResponse(): PaymentAmountValidationResponse =
    PaymentAmountValidationResponse(
        isValid = isValid,
        currentBalance = currentBalance,
        receiverName = receiverName,
        receiverImageUrl = receiverImageUrl
    )