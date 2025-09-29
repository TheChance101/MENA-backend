package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import java.time.LocalDateTime
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val senderName: String,
    val receiverName: String,
    val status: Transaction.Status,
    val type: UserTransactionType,
    val createdAt: LocalDateTime,
    val amount: Double
)

data class TransactionPageResponse(
    val totalElements: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val transactions: List<TransactionResponse>
)