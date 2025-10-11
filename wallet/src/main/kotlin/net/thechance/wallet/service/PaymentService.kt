package net.thechance.wallet.service

import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.toTransaction
import net.thechance.wallet.repository.BlockRepository
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Service
class PaymentService(
    private val pendingTransactionRepository: PendingTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val walletService: WalletService,
    private val blockRepository: BlockRepository
) {

    fun pay(userId: UUID, transactionId: UUID) {
        val pendingTransaction = pendingTransactionRepository.findById(transactionId)
            .orElseThrow { IllegalArgumentException("transaction not found") }

        if (pendingTransaction.sender.userId != userId) throw IllegalArgumentException("User is not authorized to pay this transaction")

        if (userId == pendingTransaction.receiver.userId) throw IllegalArgumentException("Sender and receiver cannot be the same")

        if (walletService.getUserBalance(userId) < pendingTransaction.amount.toDouble()) {
            val block = getToDayBlock()
            val failedTransaction = pendingTransaction.toTransaction(block, Transaction.Status.FAILED)
            transactionRepository.save(failedTransaction)
            throw IllegalArgumentException("Insufficient balance")
        }

        val existingTransaction = transactionRepository.findTransactionById(pendingTransaction.id)
        if (existingTransaction != null && existingTransaction.status == Transaction.Status.SUCCESS)
            throw IllegalArgumentException("Transaction already processed")

        val block = getToDayBlock()

        val transaction = pendingTransaction.toTransaction(block)
        transactionRepository.save(transaction)
    }

    private fun getToDayBlock(): Block {
        val today = LocalDateTime.now().toLocalDate()
        val block = blockRepository.findBlockByTimestampBetween(
            start = LocalDateTime.of(today, LocalTime.MIN),
            end = LocalDateTime.of(today, LocalTime.MAX)
        )
        return block ?: blockRepository.save(Block(previousBlockHash = "")) // TODO: set previous block hash
    }
}


