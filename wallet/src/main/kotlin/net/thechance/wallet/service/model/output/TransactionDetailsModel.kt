package net.thechance.wallet.service.model.output

import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.Transaction.Status
import net.thechance.wallet.entity.Transaction.Type
import net.thechance.wallet.entity.WalletUser
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionDetailsModel(
    val id: UUID,
    val createdAt: LocalDateTime,
    val status: Status,
    val type: Type,
    val sender: WalletUser,
    val receiver: WalletUser,
    val amount: BigDecimal,
)

fun Transaction.toTransactionDetailsModel(): TransactionDetailsModel {
    return TransactionDetailsModel(
        id = id,
        createdAt = createdAt,
        status = status,
        type = type,
        sender = sender,
        receiver = receiver,
        amount = amount,
    )
}

fun PendingTransaction.toTransactionDetailsModel(): TransactionDetailsModel {
    return TransactionDetailsModel(
        id = id,
        createdAt = createdAt,
        status = Status.PENDING,
        type = type,
        sender = sender,
        receiver = receiver,
        amount = amount,
    )
}