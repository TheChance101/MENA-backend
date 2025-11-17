package net.thechance.wallet.api.dto.balance

import java.math.BigDecimal

data class AdminDepositRequest(
    val phoneNumber : String,
    val amount: BigDecimal,
)