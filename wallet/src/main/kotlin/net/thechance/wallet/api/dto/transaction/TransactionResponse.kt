package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.api.dto.PageResponse
import net.thechance.wallet.entity.Transaction
import java.time.LocalDateTime
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val senderId: UUID,
    val senderName: String,
    val receiverId: UUID,
    val receiverName: String,
    val status: Transaction.Status,
    val type: TransactionType,
    val createdAt: LocalDateTime,
    val amount: Double
)

data class TransactionPageResponse(
    val earliestTransactionDate: LocalDateTime?,
    val transactions: PageResponse<TransactionResponse>
)