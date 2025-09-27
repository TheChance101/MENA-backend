package net.thechance.wallet.repository.transaction

import net.thechance.wallet.api.dto.transaction.TransactionType
import net.thechance.wallet.entity.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionProjection(
    val id: UUID,
    val createdAt: LocalDateTime,
    val amount: BigDecimal,
    val status: Transaction.Status,
    val senderId: UUID,
    val senderName: String,
    val receiverId: UUID,
    val receiverName: String,
    val type : String
)
