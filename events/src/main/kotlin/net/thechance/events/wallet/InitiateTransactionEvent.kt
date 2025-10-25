package net.thechance.events.wallet

import net.thechance.events.MenaEvent
import java.util.UUID
import java.util.concurrent.CompletableFuture

data class InitiateTransactionEvent (
    val type: TransactionType,
    val senderId: UUID,
    val receiverId: UUID,
    val amount: Double,
    val response: CompletableFuture<UUID>? = null
): MenaEvent {
    enum class TransactionType {
        P2P,
        ONLINE_PURCHASE
    }
}