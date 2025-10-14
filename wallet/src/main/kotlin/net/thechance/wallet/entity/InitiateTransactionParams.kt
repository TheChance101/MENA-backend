package net.thechance.wallet.entity

import net.thechance.wallet.entity.user.WalletUser
import java.util.*

data class InitiateTransactionParams(
    val type: Transaction.Type,
    val receiverId: UUID,
    val amount: Double,
)

fun InitiateTransactionParams.toPendingTransaction(
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