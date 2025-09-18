package net.thechance.wallet.api.dto

import java.util.UUID

data class BalanceDto(
    val userId: UUID,
    val balance: Double,
)