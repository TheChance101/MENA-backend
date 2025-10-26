package net.thechance.events.wallet

import net.thechance.events.MenaEvent
import java.util.*

data class InitiateTransactionEvent (
    val transactionId: UUID,
    val type: TransactionType,
    val senderId: UUID,
    val receiverId: UUID,
    val amount: Double,
): MenaEvent {
    enum class TransactionType {
        P2P,
        ONLINE_PURCHASE
    }
}