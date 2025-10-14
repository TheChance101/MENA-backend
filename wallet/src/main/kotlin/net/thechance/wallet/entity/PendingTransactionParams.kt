package net.thechance.wallet.entity

import net.thechance.wallet.entity.user.WalletUser
import java.util.*

data class PendingTransactionParams(
    val type: Transaction.Type,
    val receiverId: UUID,
    val amount: Double,
)

fun PendingTransactionParams.toPendingTransaction(
    sender: WalletUser,
    receiver: WalletUser,
): PendingTransaction {
    return PendingTransaction(
        type = type,
        sender = sender,
        receiver = receiver,
        amount = amount.toBigDecimal(),
    )
}