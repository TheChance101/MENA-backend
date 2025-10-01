package net.thechance.wallet.service

import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.helper.TransactionFilterParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
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
                ?: getUserFirstTransactionDate(currentUserId = currentUserId)
                ?: LocalDateTime.now()

        val endDate = transactionFilterParams.endDate?.atTime(23, 59, 59, 59) ?: LocalDateTime.now()

        return transactionRepository.findFilteredTransactions(
            status = transactionFilterParams.status,
            transactionTypes = transactionFilterParams.types?.map{ it.name},
            startDate = startDate,
            endDate = endDate,
            pageable = PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                Sort.by(Sort.Direction.DESC, Transaction::createdAt.name)
            ),
            currentUserId = currentUserId
        )
    }

    fun getUserFirstTransactionDate(currentUserId: UUID): LocalDateTime? {
        return transactionRepository.findFirstBySenderUserIdOrReceiverUserIdOrderByCreatedAtAsc(currentUserId, currentUserId)?.createdAt
    }

    fun getTransactionDetails(transactionId: UUID): Transaction {
        return transactionRepository.findTransactionById(
            transactionId,
        ) ?: throw EntityNotFoundException("Transaction with ID $transactionId not found or access denied.")
    }

}