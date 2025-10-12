package net.thechance.wallet.service

import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.toTransaction
import net.thechance.wallet.repository.BlockRepository
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

@Service
class PaymentService(
    private val pendingTransactionRepository: PendingTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val walletService: WalletService,
    private val blockRepository: BlockRepository
) {

    @Transactional
    fun pay(userId: UUID, transactionId: UUID) {
        val pendingTransaction = validateAndGetTransaction(userId, transactionId)

        val block = getCurrentBlock()
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

    private fun getCurrentBlock(): Block {
        val latestBlock = blockRepository.findTopByOrderByTimestampDesc()
        if (latestBlock != null) {
            val transactionCount = transactionRepository.countAllByBlockId(latestBlock.id)
            if (transactionCount < TRANSACTION_COUNT_LIMIT_PER_BLOCK) {
                return latestBlock
            }
        }
        val previousBlockHash = calculatePreviousBlockHash(latestBlock)
        return blockRepository.save(Block(previousBlockHash = previousBlockHash))
    }

    private fun calculatePreviousBlockHash(latestBlock: Block?): String {
        if (latestBlock == null) return "0".repeat(64)

        val transactionsData = transactionRepository
            .getAllByBlockId(latestBlock.id, Pageable.ofSize(TRANSACTION_COUNT_LIMIT_PER_BLOCK))
            .joinToString(separator = "|") { it.toString() }

        val input = latestBlock.id.toString() +
                latestBlock.timestamp.toString() +
                transactionsData +
                LocalDateTime.now().toString()

        return hashWithSha256(input)
    }

    private fun hashWithSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val TRANSACTION_COUNT_LIMIT_PER_BLOCK = 15
    }
}
