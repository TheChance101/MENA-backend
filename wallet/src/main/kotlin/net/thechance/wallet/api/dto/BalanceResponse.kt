package net.thechance.wallet.api.dto

import java.math.BigDecimal

data class BalanceResponse(
    val balance: BigDecimal,
)