package net.thechance.wallet.service.helper

data class PaymentAmountValidationResult(
    val isValid: Boolean,
    val currentBalance: Double,
    val recipientName: String,
    val recipientImageUrl: String? = null
)