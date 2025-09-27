package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.entity.Block
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.TransactionStatus
import net.thechance.wallet.repository.TransactionRepository
import java.nio.file.AccessDeniedException
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


class WalletServiceTest {
    private val transactionRepository = mockk<TransactionRepository>()
    private val walletService = WalletService(transactionRepository = transactionRepository)

    @Test
    fun `getUserBalance should return total user balance when it is called`() {
        every { transactionRepository.sumAmountBySenderId(USER_ID) } returns SENT
        every { transactionRepository.sumAmountByReceiverId(USER_ID) } returns RECEIVED

        val result = walletService.getUserBalance(USER_ID)

        assertThat(result).isEqualTo(EXPECTED_BALANCE)
    }

    @Test
    fun `getUserBalance should return 0 when no transactions`() {
        every { transactionRepository.sumAmountBySenderId(USER_ID) } returns null
        every { transactionRepository.sumAmountByReceiverId(USER_ID) } returns null

        val result = walletService.getUserBalance(USER_ID)

        assertThat(result).isEqualTo(BigDecimal.ZERO)
    }

    @Test(expected = EntityNotFoundException::class)
    fun `getTransactionDetails should throw EntityNotFoundException when transaction does not exist`() {
        every { transactionRepository.findById(TRANSACTION_ID) } returns Optional.empty()

        walletService.getTransactionDetails(TRANSACTION_ID, SENDER_ID)
    }
    @Test(expected = AccessDeniedException::class)
    fun `getTransactionDetails should throw AccessDeniedException when user is not part of the transaction`() {
        every { transactionRepository.findById(TRANSACTION_ID) } returns Optional.of(SAMPLE_TRANSACTION)

        val unauthorizedUserId = UUID.randomUUID()

        walletService.getTransactionDetails(TRANSACTION_ID, unauthorizedUserId)
    }

    companion object {
        private val USER_ID = UUID.fromString("a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6")
        private val RECEIVED = BigDecimal("32543.0")
        private val SENT = BigDecimal("3236.0")
        private val EXPECTED_BALANCE = RECEIVED - SENT

        private val TRANSACTION_ID = UUID.fromString("f4c3d2e1-a0b9-4b8c-8b7a-9c8d7e6f5a4b")
        private val SENDER_ID = UUID.fromString("a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6")
        private val RECEIVER_ID = UUID.fromString("b2c3d4e5-f6a7-b8c9-d0e1-f2a3b4c5d6e7")

        private val SAMPLE_TRANSACTION = Transaction(
            id = TRANSACTION_ID,
            createdAt = LocalDateTime.now(),
            senderId = SENDER_ID,
            receiverId = RECEIVER_ID,
            senderSignature = "signature",
            amount = BigDecimal("100.0"),
            block = mockk<Block>(),
            description = "Test Transaction",
            status = TransactionStatus.COMPLETED
        )
    }
}