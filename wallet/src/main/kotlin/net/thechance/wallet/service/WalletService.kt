package net.thechance.wallet.service

import net.thechance.wallet.api.dto.TransactionHistoryResponse
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.util.helperFunctions.toDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class WalletService(
    private val transactionRepository: TransactionRepository
) {

    fun getUserBalance(userId: UUID): Double {

        val totalReceived = transactionRepository.sumAmountByReceiverId(userId) ?: 0.0

        val totalSent = transactionRepository.sumAmountBySenderId(userId) ?: 0.0

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
}