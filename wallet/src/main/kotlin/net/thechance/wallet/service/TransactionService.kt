package net.thechance.wallet.service

import net.thechance.wallet.api.dto.transaction.TransactionFilterRequest
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.transaction.TransactionProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository
) {
    fun getFilteredTransactions(
        filter: TransactionFilterRequest,
        pageable: Pageable,
        currentUserId: UUID
    ): Page<TransactionProjection> {
        val startDate =
            filter.startDate
                ?: transactionRepository.findFirstByUserId(userId = currentUserId)?.createdAt
                ?: LocalDateTime.now()

        val endDate = filter.endDate ?: LocalDateTime.now()

        return transactionRepository.findFilteredTransactions(
            type = filter.type?.name,
            status = filter.status,
            startDate = startDate,
            endDate = endDate,
            pageable = pageable,
            currentUserId = currentUserId
        )
    }

    fun getUserFirstTransactionDate(currentUserId: UUID): LocalDateTime? {
        return transactionRepository.findFirstByUserId(userId = currentUserId)?.createdAt
    }
}