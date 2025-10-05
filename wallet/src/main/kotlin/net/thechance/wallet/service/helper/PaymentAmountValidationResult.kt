package net.thechance.wallet.service.helper

data class PaymentAmountValidationResult(
    val isValid: Boolean,
    val recipientName: String,
    val recipientImageUrl: String? = null
)