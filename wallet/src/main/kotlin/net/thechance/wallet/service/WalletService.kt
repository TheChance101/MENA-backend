package net.thechance.wallet.service

import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.api.dto.TransactionDetailsResponse
import net.thechance.wallet.api.dto.TransactionHistoryResponse
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.util.helperFunctions.toDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class WalletService(
    private val transactionRepository: TransactionRepository
) {

    fun getUserBalance(userId: UUID): BigDecimal {

        val totalReceived = transactionRepository.sumAmountByReceiverId(userId) ?: BigDecimal.ZERO

        val totalSent = transactionRepository.sumAmountBySenderId(userId) ?: BigDecimal.ZERO

        return totalReceived - totalSent
    }

    fun getTransactionHistory(userId: UUID, pageable: Pageable): Page<TransactionHistoryResponse> {
        val transactionsPage = transactionRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(
            userId, userId, pageable
        )
        return transactionsPage.map { transaction ->
            transaction.toDto(currentUserId = userId)
        }
    }

    fun getTransactionDetails(transactionId: UUID, currentUserId: UUID): TransactionDetailsResponse {
        // 1. Find the transaction by its ID or throw an error if not found.
        val transaction = transactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("Transaction with ID $transactionId not found") }

        // 2. Authorize: Ensure the user requesting the details is part of the transaction.
        if (transaction.senderId != currentUserId && transaction.receiverId != currentUserId) {
            throw AccessDeniedException("User is not authorized to view this transaction")
        }

        // 3. Map the entity to our detailed DTO and return it.
        return transaction.toDetailsDto()
    }
}