package net.thechance.wallet.service

import net.thechance.wallet.entity.Block
import net.thechance.wallet.repository.BlockRepository
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime

@Service
class BlockService(
    private val transactionRepository: TransactionRepository,
    private val blockRepository: BlockRepository
) {

    fun getCurrentBlock(): Block {
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
