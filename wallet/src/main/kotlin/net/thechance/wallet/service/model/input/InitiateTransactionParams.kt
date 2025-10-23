package net.thechance.wallet.service.model.input

import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import java.util.*

data class InitiateTransactionParams(
    val type: Transaction.Type,
    val senderId: UUID,
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