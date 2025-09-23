package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val senderId: UUID,
    val senderName: String,
    val receiverId: UUID,
    val receiverName: String,
    val status: Transaction.Status,
    val type: TransactionType,
    val createdAt: LocalDateTime,
    val amount: BigDecimal,
)