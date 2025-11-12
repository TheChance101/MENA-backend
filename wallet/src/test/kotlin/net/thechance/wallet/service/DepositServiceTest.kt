package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import net.thechance.wallet.exception.BlockedWalletUserException
import net.thechance.wallet.repository.TransactionRepository
import org.junit.Assert.assertThrows
import org.junit.Before
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class DepositServiceTest {
    private val transactionRepository = mockk<TransactionRepository>()
    private val blockService = mockk<BlockService>()
    private val walletUserService = mockk<WalletUserService>()

    private lateinit var adminWalletService: DepositService

    @Before
    fun setUp() {
        adminWalletService = DepositService(
            transactionRepository,
            walletUserService,
            blockService
        )
    }

    @Test
    fun `deposit throws when receiver is blocked`() {
        val blockedReceiver = receiver.copy(status = WalletUser.Status.BLOCKED)
        every { walletUserService.getUserById(senderId) } returns sender
        every { walletUserService.getUserByPhoneNumber(blockedReceiver.phoneNumber) } returns blockedReceiver
        every { blockService.getCurrentBlock() } returns block

        val exception = assertThrows(BlockedWalletUserException::class.java) {
            adminWalletService.depositMoney(senderId, blockedReceiver.phoneNumber, 50.0)
        }
        assertThat(exception.message).isEqualTo("Receiver is blocked")
    }

    @Test
    fun `deposit throws when receiver not found`() {
        every { walletUserService.getUserById(senderId) } returns sender
        every {
            walletUserService.getUserByPhoneNumber(nonExistentPhone)
        } throws IllegalArgumentException("User with phone number $nonExistentPhone not found")
        every { blockService.getCurrentBlock() } returns block

        val exception = assertThrows(IllegalArgumentException::class.java) {
            adminWalletService.depositMoney(senderId, nonExistentPhone, 10.0)
        }

        assertThat(exception.message).isEqualTo("User with phone number $nonExistentPhone not found")
    }

    @Test
    fun `deposit creates a successful transaction`() {
        every { walletUserService.getUserById(senderId) } returns sender
        every { walletUserService.getUserByPhoneNumber(receiver.phoneNumber) } returns receiver
        every { blockService.getCurrentBlock() } returns block
        every { transactionRepository.save(any()) } answers { firstArg() }

        adminWalletService.depositMoney(senderId, receiver.phoneNumber, 100.0)

        verify(exactly = 1) { blockService.getCurrentBlock() }
        verify(exactly = 1) { walletUserService.getUserById(senderId) }
        verify(exactly = 1) { walletUserService.getUserByPhoneNumber(receiver.phoneNumber) }
        verify(exactly = 1) { transactionRepository.save(any()) }

        val slotTransaction = slot<Transaction>()
        verify { transactionRepository.save(capture(slotTransaction)) }

        val savedTransaction = slotTransaction.captured
        assertThat(savedTransaction.amount).isEqualTo(BigDecimal.valueOf(100.0))
        assertThat(savedTransaction.sender).isEqualTo(sender)
        assertThat(savedTransaction.receiver).isEqualTo(receiver)
        assertThat(savedTransaction.block).isEqualTo(block)
        assertThat(savedTransaction.status).isEqualTo(Transaction.Status.SUCCESS)
        assertThat(savedTransaction.type).isEqualTo(Transaction.Type.DEPOSIT)
    }

    private companion object {
        val senderId = UUID.randomUUID()
        val receiverId = UUID.randomUUID()
        val blockId = UUID.randomUUID()
        val nonExistentPhone = "01111111111"

        val receiver = WalletUser(
            userId = receiverId,
            firstName = "Receiver",
            lastName = "User",
            phoneNumber = "01000000002",
            imageUrl = null,
            status = WalletUser.Status.ACTIVE
        )

        val block = Block(
            id = blockId,
            previousBlockHash = "prev-hash",
            timestamp = LocalDateTime.now()
        )

        val sender = WalletUser(
            userId = senderId,
            firstName = "Sender",
            lastName = "User",
            phoneNumber = "01000000001",
            imageUrl = null,
            status = WalletUser.Status.ACTIVE
        )
    }
}