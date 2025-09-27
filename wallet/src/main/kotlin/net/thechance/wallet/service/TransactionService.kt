package net.thechance.wallet.service

import net.thechance.wallet.api.dto.transaction.TransactionType
import net.thechance.wallet.entity.Transaction
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
        type: TransactionType?,
        status: Transaction.Status?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        pageable: Pageable,
        currentUserId: UUID
    ): Page<TransactionProjection> {
        val startDateValue =
            startDate
                ?: transactionRepository.findFirstByUserId(userId = currentUserId)?.createdAt
                ?: LocalDateTime.now()

        val endDateValue = endDate ?: LocalDateTime.now()

        return transactionRepository.findFilteredTransactions(
            type = type?.name,
            status = status,
            startDate = startDateValue,
            endDate = endDateValue,
            pageable = pageable,
            currentUserId = currentUserId
        )
    }

    fun getUserFirstTransactionDate(currentUserId: UUID): LocalDateTime? {
        return transactionRepository.findFirstByUserId(userId = currentUserId)?.createdAt
    }
}