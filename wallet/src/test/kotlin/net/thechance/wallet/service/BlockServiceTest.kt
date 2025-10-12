package net.thechance.wallet.service

import io.mockk.*
import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.repository.BlockRepository
import net.thechance.wallet.repository.TransactionRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime
import java.util.*

class BlockServiceTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var blockRepository: BlockRepository
    private lateinit var blockService: BlockService

    @Before
    fun setUp() {
        transactionRepository = mockk()
        blockRepository = mockk()
        blockService = BlockService(transactionRepository, blockRepository)
    }

    @Test
    fun `should return latest block if transaction count is less than limit`() {
        val blockId = UUID.randomUUID()
        val latestBlock = Block(id = blockId, previousBlockHash = "abc", timestamp = LocalDateTime.now())
        every { blockRepository.findTopByOrderByTimestampDesc() } returns latestBlock
        every { transactionRepository.countAllByBlockId(blockId) } returns 5L

        val result = blockService.getCurrentBlock()

        assertEquals(latestBlock, result)
        verify(exactly = 1) { blockRepository.findTopByOrderByTimestampDesc() }
        verify(exactly = 1) { transactionRepository.countAllByBlockId(blockId) }
        confirmVerified(blockRepository, transactionRepository)
    }

    @Test
    fun `should create new block if transaction count is at limit`() {
        val blockId = UUID.randomUUID()
        val latestBlock = Block(id = blockId, previousBlockHash = "abc", timestamp = LocalDateTime.now())
        every { blockRepository.findTopByOrderByTimestampDesc() } returns latestBlock
        every { transactionRepository.countAllByBlockId(blockId) } returns 15L
        every { transactionRepository.getAllByBlockId(eq(blockId), any()) } returns listOf()
        every { blockRepository.save(any()) } answers { firstArg() }

        val result = blockService.getCurrentBlock()

        assertNotNull(result)
        assertNotEquals(latestBlock, result)
        assertEquals(64, result.previousBlockHash.length)
        verify { blockRepository.save(any()) }
    }

    @Test
    fun `should create new block if no previous block exists`() {
        every { blockRepository.findTopByOrderByTimestampDesc() } returns null
        every { blockRepository.save(any()) } answers { firstArg() }

        val result = blockService.getCurrentBlock()

        assertNotNull(result)
        assertEquals("0".repeat(64), result.previousBlockHash)
        verify { blockRepository.save(any()) }
    }

    @Test
    fun `should calculate previous block hash with transactions`() {
        val blockId = UUID.randomUUID()
        val latestBlock = Block(id = blockId, previousBlockHash = "abc", timestamp = LocalDateTime.now())
        val transaction = mockk<Transaction>()
        every { blockRepository.findTopByOrderByTimestampDesc() } returns latestBlock
        every { transactionRepository.countAllByBlockId(blockId) } returns 15L
        every { transactionRepository.getAllByBlockId(eq(blockId), any()) } returns listOf(transaction, transaction)
        every { transaction.toString() } returns "tx"
        every { blockRepository.save(any()) } answers { firstArg() }

        val result = blockService.getCurrentBlock()

        assertNotNull(result)
        assertEquals(64, result.previousBlockHash.length)
        verify { blockRepository.save(any()) }
    }
}
