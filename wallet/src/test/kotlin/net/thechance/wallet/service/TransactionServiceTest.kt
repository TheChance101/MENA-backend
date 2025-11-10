package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.WalletUser
import net.thechance.wallet.repository.PendingTransactionRepository
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.model.output.toTransactionDetailsModel
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class TransactionServiceTest {

    private val transactionRepository = mockk<TransactionRepository>()
    private val pendingTransactionRepository = mockk<PendingTransactionRepository>()
    private val transactionService = TransactionService(transactionRepository = transactionRepository, pendingTransactionRepository = pendingTransactionRepository, walletUserService = mockk())

    @Test
    fun `getTransactionDetails should return transaction when it exists and user is authorized`() {
        every {
            transactionRepository.findByIdOrNull(
                TRANSACTION_ID
            )
        } returns FAKE_TRANSACTION

        val result = transactionService.getTransactionDetails(TRANSACTION_ID)

        assertThat(result).isEqualTo(FAKE_TRANSACTION.toTransactionDetailsModel())
    }

    @Test
    fun `getTransactionDetails should throw EntityNotFoundException when transaction does not exist or user is not authorized`() {
        val transactionId = UUID.randomUUID()
        every {
            transactionRepository.findByIdOrNull(
                transactionId
            )
        } returns null

        every {
            pendingTransactionRepository.findByIdOrNull(
                transactionId
            )
        } returns null

        val exception = assertThrows(EntityNotFoundException::class.java) {
            transactionService.getTransactionDetails(transactionId)
        }

        assertThat(exception.message).isEqualTo("Transaction with ID $transactionId not found or access denied.")
    }

    @Test
    fun `getUserFirstTransactionDate should return date when transactions exist`() {
        every {
            transactionRepository.findFirstBySenderUserIdOrReceiverUserIdOrderByCreatedAtAsc(
                senderId = USER_ID, receiverId = USER_ID
            )
        } returns FAKE_TRANSACTION

        val result = transactionService.getUserFirstTransactionDate(USER_ID)

        assertThat(result).isEqualTo(FAKE_TRANSACTION.createdAt)
    }

    @Test
    fun `getUserFirstTransactionDate should return null when no transactions exist`() {
        every {
            transactionRepository.findFirstBySenderUserIdOrReceiverUserIdOrderByCreatedAtAsc(
                senderId = USER_ID,
                receiverId = USER_ID
            )
        } returns null

        val result = transactionService.getUserFirstTransactionDate(USER_ID)

        assertThat(result).isNull()
    }

    private companion object {
        val USER_ID: UUID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef")
        val OTHER_USER_ID: UUID = UUID.fromString("f1e2d3c4-b5a6-0987-6543-210987fedcba")
        val TRANSACTION_ID: UUID = UUID.fromString("11223344-5566-7788-9900-aabbccddeeff")


        val FAKE_TRANSACTION = Transaction(
            id = TRANSACTION_ID,
            sender = WalletUser(userId = USER_ID, firstName = "First", lastName = "Last", imageUrl = null, status = WalletUser.Status.ACTIVE),
            receiver = WalletUser(userId = OTHER_USER_ID, firstName = "Other", lastName = "User", imageUrl = null, status = WalletUser.Status.ACTIVE),
            amount = BigDecimal.TEN,
            createdAt = LocalDateTime.now().minusDays(5),
            status = Transaction.Status.SUCCESS,
            type = Transaction.Type.P2P,
            block = mockk()
        )
    }
}