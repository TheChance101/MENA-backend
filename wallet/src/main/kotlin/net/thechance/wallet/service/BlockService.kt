package net.thechance.wallet.service

import net.thechance.wallet.entity.Block
import net.thechance.wallet.repository.BlockRepository
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

@Service
class BlockService(
    private val transactionRepository: TransactionRepository,
    private val blockRepository: BlockRepository
) {

    fun getCurrentBlock(): Block {
        val latestBlock = blockRepository.findTopByOrderByTimestampDesc()
        return if (latestBlock != null && !isBlockFull(latestBlock)) {
            latestBlock
        } else {
            createNewBlock(latestBlock)
        }
    }

    private fun isBlockFull(block: Block): Boolean {
        val transactionCount = transactionRepository.countAllByBlockId(block.id)
        return transactionCount >= TRANSACTION_COUNT_LIMIT_PER_BLOCK
    }

    private fun createNewBlock(previousBlock: Block?): Block {
        val previousHash = previousBlock?.let { calculatePreviousBlockHash(it) } ?: "0".repeat(64)
        return blockRepository.save(Block(previousBlockHash = previousHash))
    }

    private fun calculatePreviousBlockHash(latestBlock: Block): String {
        val transactionsData = getTransactionsData(latestBlock.id)

        val input = latestBlock.id.toString() +
                latestBlock.timestamp.toString() +
                transactionsData +
                LocalDateTime.now().toString()

        return hashWithSha256(input)
    }

    private fun getTransactionsData(blockId: UUID): String {
        val transactions = transactionRepository
            .getAllByBlockId(blockId, Pageable.ofSize(TRANSACTION_COUNT_LIMIT_PER_BLOCK))
        return transactions.joinToString(separator = "|") { it.toString() }
    }

    private fun hashWithSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val TRANSACTION_COUNT_LIMIT_PER_BLOCK = 100
    }
}
