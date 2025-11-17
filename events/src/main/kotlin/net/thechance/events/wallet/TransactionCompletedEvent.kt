package net.thechance.events.wallet

import net.thechance.events.MenaEvent
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionCompletedEvent(
    val transactionId: UUID,
    val createdAt: LocalDateTime,
    val status: TransactionStatus,
    val type: TransactionType,
    val senderId: UUID,
    val receiverId: UUID,
    val amount: BigDecimal
) : MenaEvent {

    enum class TransactionType {
        P2P,
        ONLINE_PURCHASE,
        DEPOSIT
    }

    enum class TransactionStatus {
        FAILED,
        SUCCESS,
        PENDING
    }
}