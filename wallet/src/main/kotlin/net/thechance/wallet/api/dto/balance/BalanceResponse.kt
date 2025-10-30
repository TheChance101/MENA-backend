package net.thechance.wallet.api.dto.balance

data class BalanceResponse(
    val balance: Double
)

fun Double.toBalanceResponse(): BalanceResponse {
    return BalanceResponse(balance = this)
}