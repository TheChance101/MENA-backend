package net.thechance.wallet.service

import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.entity.*
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.WalletUserRepository
import net.thechance.wallet.service.helper.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val pendingTransactionRepository: PendingTransactionRepository,
    private val walletUserRepository: WalletUserRepository,
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
            transactionTypes = transactionFilterParams.types?.map { it.name },
            startDate = startDate,
            endDate = endDate,
            pageable = pageable,
            currentUserId = currentUserId
        )
    }

    fun getUserFirstTransactionDate(currentUserId: UUID): LocalDateTime? {
        return transactionRepository.findFirstBySenderUserIdOrReceiverUserIdOrderByCreatedAtAsc(
            currentUserId,
            currentUserId
        )?.createdAt
    }

    fun getTransactionDetails(transactionId: UUID): Transaction {
        return transactionRepository.findTransactionById(
            transactionId,
        ) ?: throw EntityNotFoundException("Transaction with ID $transactionId not found or access denied.")
    }

    fun createTransaction(transaction: PendingTransactionParams, senderId: UUID): PendingTransaction {
        if (transaction.receiverId == senderId)
            throw IllegalArgumentException("Sender and receiver cannot be the same.")

        val sender = walletUserRepository.getReferenceById(senderId)
        val receiver = walletUserRepository.getReferenceById(transaction.receiverId)
        return pendingTransactionRepository.save(transaction.toPendingTransaction(sender, receiver))
    }

    fun getTransactionReceiverDetails(transactionId: UUID): ReceiverDetails {
        val pendingTransaction = pendingTransactionRepository.findByIdOrNull(transactionId)
        val transaction = transactionRepository.findByIdOrNull(transactionId)

        val receiver = transaction?.receiver ?: pendingTransaction?.receiver
        ?: throw EntityNotFoundException("Transaction ID $transactionId not found.")

        return receiver.toReceiverDetails(transaction?.type ?: pendingTransaction?.type ?: Transaction.Type.P2P)
    }
}