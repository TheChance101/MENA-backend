package net.thechance.wallet.api.dto.balance

import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class AdminDepositRequest(
    val phoneNumber : String,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    val amount: BigDecimal,
)