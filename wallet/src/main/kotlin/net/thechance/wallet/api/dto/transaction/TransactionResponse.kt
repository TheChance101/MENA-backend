package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.api.controller.util.ImageUrlBuilder
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

fun Page<Transaction>.toResponsePage(currentUserId: UUID, imageUrlBuilder: ImageUrlBuilder): PageResponse<TransactionResponse> {
    return PageResponse(
        items = this.content.map { it.toResponse(currentUserId, imageUrlBuilder) },
        page = this.number,
        pageSize = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages
    )
}

private fun Transaction.toResponse(currentUserId: UUID, imageUrlBuilder: ImageUrlBuilder): TransactionResponse {
    return TransactionResponse(
        id = id,
        sender = getSenderInfo(type, sender, imageUrlBuilder),
        receiver = getReceiverInfo(
            type = type,
            senderUserId = sender.userId,
            currentUserId = currentUserId,
            receiver = receiver,
            imageUrlBuilder = imageUrlBuilder
        ),
        status = status,
        type = getUserType(type, sender.userId, currentUserId),
        createdAt = createdAt,
        amount = amount.toDouble()
    )
}

fun TransactionDetailsModel.toResponse(currentUserId: UUID, imageUrlBuilder: ImageUrlBuilder): TransactionResponse {
    return TransactionResponse(
        id = id,
        sender = getSenderInfo(type, sender, imageUrlBuilder),
        receiver = getReceiverInfo(
            type = type,
            senderUserId = sender.userId,
            currentUserId = currentUserId,
            receiver = receiver,
            imageUrlBuilder = imageUrlBuilder
        ),
        status = status,
        type = getUserType(type, sender.userId, currentUserId),
        createdAt = createdAt,
        amount = amount.toDouble()
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
        type == Transaction.Type.DEPOSIT -> UserTransactionType.DEPOSIT
        else -> UserTransactionType.RECEIVED
    }
}

private fun getSenderInfo(
    type: Transaction.Type,
    sender: WalletUser,
    imageUrlBuilder: ImageUrlBuilder
): TransactionPartyInfo {
    val isDeposit = type == Transaction.Type.DEPOSIT
    val name = if (isDeposit) "MENA" else sender.userName
    val imageUrl = if (isDeposit) null else sender.imageUrl

    return TransactionPartyInfo(
        name = name,
        imageUrl = imageUrlBuilder.buildUserImageUrl(imageUrl)
    )
}

private fun getReceiverInfo(
    type: Transaction.Type,
    senderUserId: UUID,
    currentUserId: UUID,
    receiver: WalletUser,
    imageUrlBuilder: ImageUrlBuilder
): TransactionPartyInfo {
    val isPurchaseFromCurrentUser = type == Transaction.Type.ONLINE_PURCHASE && senderUserId == currentUserId
    val name = when {
        isPurchaseFromCurrentUser && receiver.dukan?.imageUrl?.isNotBlank() == true -> receiver.dukan.name
        else -> receiver.userName
    }
    val imageUrl = when {
        isPurchaseFromCurrentUser && receiver.dukan?.imageUrl?.isNotBlank() == true -> receiver.dukan.imageUrl
        else -> imageUrlBuilder.buildUserImageUrl(receiver.imageUrl)
    }

    return TransactionPartyInfo(name = name, imageUrl = imageUrl)
}