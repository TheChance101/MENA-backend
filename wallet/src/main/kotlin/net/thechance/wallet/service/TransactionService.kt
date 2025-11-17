package net.thechance.wallet.service

import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.model.input.InitiateTransactionParams
import net.thechance.wallet.service.model.input.TransactionFilterParams
import net.thechance.wallet.service.model.input.toPendingTransaction
import net.thechance.wallet.service.model.output.TransactionDetailsModel
import net.thechance.wallet.service.model.output.toTransactionDetailsModel
import net.thechance.wallet.service.utils.orNow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*


@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val pendingTransactionRepository: PendingTransactionRepository,
    private val walletUserService: WalletUserService,
) {
    fun getFilteredTransactions(
        transactionFilterParams: TransactionFilterParams,
        currentUserId: UUID,
        pageable: Pageable,
    ): Page<Transaction> {

        val startDate = transactionFilterParams.startDateTime
            ?: getUserFirstTransactionDate(currentUserId = currentUserId).orNow()

        val endDate = transactionFilterParams.endDateTime.orNow()

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

    fun getTransactionDetails(transactionId: UUID): TransactionDetailsModel {
        return transactionRepository.findByIdOrNull(transactionId)?.toTransactionDetailsModel()
            ?: pendingTransactionRepository.findByIdOrNull(transactionId)?.toTransactionDetailsModel()
            ?: throw EntityNotFoundException("Transaction with ID $transactionId not found or access denied.")
    }

    fun initiateTransaction(initiateTransactionParams: InitiateTransactionParams): PendingTransaction {
        if (initiateTransactionParams.receiverId == initiateTransactionParams.senderId)
            throw IllegalArgumentException("Sender and receiver cannot be the same.")

        val sender = walletUserService.getUserById(initiateTransactionParams.senderId)
        val receiver = walletUserService.getUserById(initiateTransactionParams.receiverId)

        validateUsersStatus(sender, receiver)
        
        return pendingTransactionRepository.save(initiateTransactionParams.toPendingTransaction(sender, receiver))
    }

    @Transactional
    fun clearExpiredPendingTransactions(expirationTime: LocalDateTime) {
        val expiredTransactions = pendingTransactionRepository.deleteAllByCreatedAtBefore(expirationTime)
    }

    private fun validateUsersStatus(sender: WalletUser, receiver: WalletUser) {
        when {
            sender.isBlocked() -> throw IllegalArgumentException("Sender is blocked.")
            receiver.isBlocked() -> throw IllegalArgumentException("Receiver is blocked.")
        }
    }
}