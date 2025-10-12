package net.thechance.wallet.service

import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.toTransaction
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PaymentService(
    private val pendingTransactionRepository: PendingTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val walletService: WalletService,
    private val blockService: BlockService
) {

    @Transactional
    fun pay(userId: UUID, transactionId: UUID) {
        val pendingTransaction = validateAndGetTransaction(userId, transactionId)

        val block = blockService.getCurrentBlock()
        val transaction = pendingTransaction.toTransaction(block)
        transactionRepository.save(transaction)
        pendingTransactionRepository.deleteById(transactionId)
    }

    private fun validateAndGetTransaction(userId: UUID, transactionId: UUID): PendingTransaction {
        if (transactionRepository.findByIdOrNull(transactionId) != null)
            throw IllegalArgumentException("Transaction already processed")

        val pendingTransaction = pendingTransactionRepository.findById(transactionId)
            .orElseThrow { IllegalArgumentException("transaction not found") }

        if (pendingTransaction.sender.userId != userId)
            throw IllegalArgumentException("User is not authorized to pay this transaction")

        if (userId == pendingTransaction.receiver.userId)
            throw IllegalArgumentException("Sender and receiver cannot be the same")

        if (walletService.getUserBalance(userId) < pendingTransaction.amount.toDouble())
            throw IllegalArgumentException("Insufficient balance")

        return pendingTransaction
    }
}
