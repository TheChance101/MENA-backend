package net.thechance.wallet.service.helper

data class PaymentAmountValidationResult(
    val isValid: Boolean,
    val currentBalance: Double,
    val receiverName: String,
    val receiverImageUrl: String? = null
)