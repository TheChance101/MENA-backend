package net.thechance.wallet.service

import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.helper.TransactionFilterParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.nio.file.AccessDeniedException
import java.time.LocalDateTime
import java.util.*


@Service
class TransactionService(
    private val transactionRepository: TransactionRepository
) {
    fun getFilteredTransactions(
        transactionFilterParams: TransactionFilterParams,
        currentUserId: UUID,
        pageable: Pageable,
    ): Page<Transaction> {

        val startDate =
            transactionFilterParams.startDate?.atStartOfDay()
                ?: transactionRepository.findFirstBySender_UserIdOrReceiver_UserIdOrderByCreatedAtAsc(
                    senderId = currentUserId,
                    receiverId = currentUserId
                )?.createdAt
                ?: LocalDateTime.now()

        val endDate = transactionFilterParams.endDate?.atTime(23, 59, 59, 59) ?: LocalDateTime.now()

        return transactionRepository.findFilteredTransactions(
            status = transactionFilterParams.status,
            transactionType = transactionFilterParams.type?.name,
            startDate = startDate,
            endDate = endDate,
            pageable = pageable,
            currentUserId = currentUserId
        )
    }

    fun getUserFirstTransactionDate(currentUserId: UUID): LocalDateTime? {
        return transactionRepository.findFirstBySender_UserIdOrReceiver_UserIdOrderByCreatedAtAsc(
            senderId = currentUserId,
            receiverId = currentUserId
        )?.createdAt
    }

    fun getTransactionDetails(transactionId: UUID, currentUserId: UUID): Transaction {
        return transactionRepository.findFirstBySender_UserIdOrReceiver_UserIdOrderByCreatedAtAsc(
            transactionId,
            currentUserId,
            currentUserId
        ) ?: throw EntityNotFoundException("Transaction with ID $transactionId not found or access denied.")
    }

}