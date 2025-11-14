package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.api.dto.PageResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import net.thechance.wallet.service.model.input.UserTransactionType
import net.thechance.wallet.service.model.output.TransactionDetailsModel
import org.springframework.data.domain.Page
import java.time.LocalDateTime
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val sender: TransactionPartyInfo,
    val receiver: TransactionPartyInfo,
    val status: Transaction.Status,
    val type: UserTransactionType,
    val createdAt: LocalDateTime,
    val amount: Double
)

data class TransactionPartyInfo(
    val name: String,
    val imageUrl: String?
)

fun Page<Transaction>.toResponsePage(currentUserId: UUID): PageResponse<TransactionResponse> {
    return PageResponse(
        items = this.content.map { it.toResponse(currentUserId) },
        page = this.number,
        pageSize = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages
    )
}

private fun Transaction.toResponse(currentUserId: UUID): TransactionResponse {
    return TransactionResponse(
        id = id,
        sender = sender.toPartyInfo(),
        receiver = getReceiverInfo(
            type = type,
            senderUserId = sender.userId,
            currentUserId = currentUserId,
            receiver = receiver
        ),
        status = status,
        type = getUserType(type, sender.userId, currentUserId),
        createdAt = createdAt,
        amount = amount.toDouble()
    )
}

fun TransactionDetailsModel.toResponse(currentUserId: UUID): TransactionResponse {
    return TransactionResponse(
        id = id,
        sender = sender.toPartyInfo(),
        receiver = getReceiverInfo(
            type = type,
            senderUserId = sender.userId,
            currentUserId = currentUserId,
            receiver = receiver
        ),
        status = status,
        type = getUserType(type, sender.userId, currentUserId),
        createdAt = createdAt,
        amount = amount.toDouble()
    )
}

private fun WalletUser.toPartyInfo(): TransactionPartyInfo {
    return TransactionPartyInfo(
        name = userName,
        imageUrl = imageUrl
    )
}

private fun getUserType(
    type: Transaction.Type,
    senderUserId: UUID,
    currentUserId: UUID
): UserTransactionType {
    return when {
        type == Transaction.Type.P2P && senderUserId == currentUserId -> UserTransactionType.SENT
        type == Transaction.Type.ONLINE_PURCHASE && senderUserId == currentUserId -> UserTransactionType.ONLINE_PURCHASE
        else -> UserTransactionType.RECEIVED
    }
}

private fun getReceiverInfo(
    type: Transaction.Type,
    senderUserId: UUID,
    currentUserId: UUID,
    receiver: WalletUser,
): TransactionPartyInfo {
    val isPurchaseFromCurrentUser = type == Transaction.Type.ONLINE_PURCHASE && senderUserId == currentUserId

    val name = when {
        isPurchaseFromCurrentUser -> receiver.dukan?.name?.takeIf { it.isNotBlank() } ?: receiver.userName
        else -> receiver.userName
    }

    val imageUrl = when {
        isPurchaseFromCurrentUser -> receiver.dukan?.imageUrl?.takeIf { it.isNotBlank() } ?: receiver.imageUrl
        else -> receiver.imageUrl
    }

    return TransactionPartyInfo(name = name, imageUrl = imageUrl)
}