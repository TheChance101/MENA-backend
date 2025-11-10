package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.PendingTransaction
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class PaymentServiceTest {
    private val pendingTransactionRepository = mockk<PendingTransactionRepository>()
    private val transactionRepository = mockk<TransactionRepository>()
    private val balanceService = mockk<BalanceService>()
    private val blockService = mockk<BlockService>()
    private lateinit var paymentService: PaymentService

    private val userId = UUID.randomUUID()
    private val receiverId = UUID.randomUUID()
    private val transactionId = UUID.randomUUID()
    private val blockId = UUID.randomUUID()
    private val block = Block(id = blockId, previousBlockHash = "prev", timestamp = LocalDateTime.now())
    private val sender = WalletUser(userId = userId, firstName = "Sender", lastName = "User", imageUrl = null, status = WalletUser.Status.ACTIVE)
    private val receiver = WalletUser(userId = receiverId, firstName = "Receiver", lastName = "User", imageUrl = null, status = WalletUser.Status.ACTIVE)
    private val pendingTransaction = PendingTransaction(
        id = transactionId,
        sender = sender,
        receiver = receiver,
        amount = BigDecimal.TEN,
        createdAt = LocalDateTime.now(),
        type = Transaction.Type.P2P
    )
    private val transaction = Transaction(
        id = transactionId,
        sender = sender,
        receiver = receiver,
        amount = BigDecimal.TEN,
        createdAt = LocalDateTime.now(),
        status = Transaction.Status.SUCCESS,
        type = Transaction.Type.P2P,
        block = block
    )

    @Before
    fun setUp() {
        paymentService = PaymentService(
            pendingTransactionRepository,
            transactionRepository,
            balanceService,
            blockService
        )
    }

    @Test
    fun `pay throws if transaction already processed`() {
        every { transactionRepository.existsById(transactionId) } returns true
        val ex = assertThrows(IllegalArgumentException::class.java) {
            paymentService.pay(userId, transactionId)
        }
        assertThat(ex.message).isEqualTo("Transaction already processed")
    }

    @Test
    fun `pay throws if pending transaction not found`() {
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.empty()
        val ex = assertThrows(IllegalArgumentException::class.java) {
            paymentService.pay(userId, transactionId)
        }
        assertThat(ex.message).isEqualTo("transaction not found")
    }

    @Test
    fun `pay throws if user is not authorized`() {
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.of(pendingTransaction.copy(sender = WalletUser(UUID.randomUUID(), "other", "Other", "User", null, WalletUser.Status.ACTIVE)))
        val ex = assertThrows(IllegalArgumentException::class.java) {
            paymentService.pay(userId, transactionId)
        }
        assertThat(ex.message).isEqualTo("User is not authorized to pay this transaction")
    }

    @Test
    fun `pay throws if sender and receiver are the same`() {
        val sameUser = WalletUser(userId, "same", "Same", "User", null, WalletUser.Status.ACTIVE)
        val pt = pendingTransaction.copy(sender = sameUser, receiver = sameUser)
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.of(pt)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            paymentService.pay(userId, transactionId)
        }
        assertThat(ex.message).isEqualTo("Sender and receiver cannot be the same")
    }

    @Test
    fun `pay throws if insufficient balance`() {
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.of(pendingTransaction)
        every { balanceService.getUserBalance(userId) } returns 5.0
        val ex = assertThrows(IllegalArgumentException::class.java) {
            paymentService.pay(userId, transactionId)
        }
        assertThat(ex.message).isEqualTo("Insufficient balance")
    }

    @Test
    fun `pay uses existing block if not full`() {
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.of(pendingTransaction)
        every { balanceService.getUserBalance(userId) } returns 100.0
        every { blockService.getCurrentBlock() } returns block
        every { transactionRepository.save(any()) } returns transaction
        every { pendingTransactionRepository.deleteById(transactionId) } just Runs

        paymentService.pay(userId, transactionId)
        verify { transactionRepository.save(any()) }
        verify { pendingTransactionRepository.deleteById(transactionId) }
    }

    @Test
    fun `pay creates new block if last block is full`() {
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.of(pendingTransaction)
        every { balanceService.getUserBalance(userId) } returns 100.0
        every { blockService.getCurrentBlock() } returns block.copy(id = UUID.randomUUID())
        every { transactionRepository.save(any()) } returns transaction
        every { pendingTransactionRepository.deleteById(transactionId) } just Runs

        paymentService.pay(userId, transactionId)
        verify { blockService.getCurrentBlock() }
        verify { transactionRepository.save(any()) }
        verify { pendingTransactionRepository.deleteById(transactionId) }
    }

    @Test
    fun `pay creates new block if no previous block exists`() {
        every { transactionRepository.existsById(transactionId) } returns false
        every { pendingTransactionRepository.findById(transactionId) } returns Optional.of(pendingTransaction)
        every { balanceService.getUserBalance(userId) } returns 100.0
        every { blockService.getCurrentBlock() } returns block
        every { transactionRepository.save(any()) } returns transaction
        every { pendingTransactionRepository.deleteById(transactionId) } just Runs

        paymentService.pay(userId, transactionId)
        verify { blockService.getCurrentBlock() }
        verify { transactionRepository.save(any()) }
        verify { pendingTransactionRepository.deleteById(transactionId) }
    }
}
