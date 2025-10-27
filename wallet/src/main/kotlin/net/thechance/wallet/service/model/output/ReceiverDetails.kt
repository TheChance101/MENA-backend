package net.thechance.wallet.service.model.output

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser

data class ReceiverDetails(
    val name: String,
    val imageUrl: String?,
)

fun WalletUser.toReceiverDetails(type: Transaction.Type): ReceiverDetails {
    val isOnlinePurchase = (type == Transaction.Type.ONLINE_PURCHASE)
    val receiverName = dukan?.name?.takeIf { isOnlinePurchase } ?: userName
    val receiverImageUrl = if (isOnlinePurchase) dukan?.imageUrl else imageUrl
    return ReceiverDetails(
        name = receiverName,
        imageUrl = receiverImageUrl
    )
}