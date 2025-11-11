package net.thechance.wallet.api.dto.balance

import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import java.math.BigDecimal
import java.util.*

data class DepositRequest(
    val phoneNumber : String,
    val amount: Double,
)

fun DepositRequest.toTransaction(
    sender: WalletUser,
    receiver: WalletUser,
    block: Block
): Transaction {
    return Transaction(
        id = UUID.randomUUID(),
        sender = sender,
        receiver = receiver,
        amount = BigDecimal.valueOf(this.amount),
        block = block,
        status = Transaction.Status.SUCCESS,
        type = Transaction.Type.DEPOSIT
    )
}
