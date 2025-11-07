package net.thechance.wallet.service

import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.toTransaction
import net.thechance.wallet.exception.WalletUserIsBlockedException
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PaymentService(
    private val pendingTransactionRepository: PendingTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val balanceService: BalanceService,
    private val blockService: BlockService
) {

    @Transactional
    fun pay(userId: UUID, transactionId: UUID) {
        val pendingTransaction = validateAndGetTransaction(userId, transactionId)
        val transactionStatus = getTransactionStatus(pendingTransaction)
        val block = blockService.getCurrentBlock()

        val transaction = pendingTransaction.toTransaction(block, transactionStatus)
        transactionRepository.save(transaction)
        pendingTransactionRepository.deleteById(transactionId)

        if(transactionStatus == Transaction.Status.FAILED) {
            throw WalletUserIsBlockedException("Either sender or receiver is blocked")
        }
    }

    private fun getTransactionStatus(pendingTransaction: PendingTransaction): Transaction.Status {
        return when {
            pendingTransaction.sender.isBlocked() -> Transaction.Status.FAILED
            pendingTransaction.receiver.isBlocked() -> Transaction.Status.FAILED
            else -> Transaction.Status.SUCCESS
        }
    }

    private fun validateAndGetTransaction(userId: UUID, transactionId: UUID): PendingTransaction {
        if (transactionRepository.existsById(transactionId))
            throw IllegalArgumentException("Transaction already processed")

        val pendingTransaction = pendingTransactionRepository.findById(transactionId)
            .orElseThrow { IllegalArgumentException("transaction not found") }

        if (pendingTransaction.sender.userId != userId)
            throw IllegalArgumentException("User is not authorized to pay this transaction")

        if (userId == pendingTransaction.receiver.userId)
            throw IllegalArgumentException("Sender and receiver cannot be the same")

        if (balanceService.getUserBalance(userId) < pendingTransaction.amount.toDouble())
            throw IllegalArgumentException("Insufficient balance")

        return pendingTransaction
    }
}
