package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.api.dto.PageResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.model.input.UserTransactionType
import net.thechance.wallet.service.model.output.TransactionDetailsModel
import org.springframework.data.domain.Page
import java.time.LocalDateTime
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val senderName: String,
    val receiverName: String,
    val status: Transaction.Status,
    val type: UserTransactionType,
    val createdAt: LocalDateTime,
    val amount: Double
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
        senderName = sender.userName,
        receiverName = getReceiverName(
            type = type,
            senderUserId = sender.userId,
            currentUserId = currentUserId,
            receiverUserName = receiver.userName,
            receiverDukanName = receiver.dukan?.name
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
        senderName = sender.userName,
        receiverName = getReceiverName(
            type = type,
            senderUserId = sender.userId,
            currentUserId = currentUserId,
            receiverUserName = receiver.userName,
            receiverDukanName = receiver.dukan?.name
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
        else -> UserTransactionType.RECEIVED
    }
}

private fun getReceiverName(
    type: Transaction.Type,
    senderUserId: UUID,
    currentUserId: UUID,
    receiverUserName: String,
    receiverDukanName: String?
): String {
    return if (type == Transaction.Type.ONLINE_PURCHASE && senderUserId == currentUserId) {
        receiverDukanName?.ifBlank { receiverUserName } ?: receiverUserName
    } else {
        receiverUserName
    }
}