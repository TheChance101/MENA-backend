package net.thechance.wallet.api.dto.balance

data class AdminDepositRequest(
    val phoneNumber : String,
    val amount: Double,
)