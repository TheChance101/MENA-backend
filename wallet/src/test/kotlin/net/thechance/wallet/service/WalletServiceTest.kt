package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.wallet.entity.user.WalletUser
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.WalletUserRepository
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.*

class WalletServiceTest {
    private val transactionRepository = mockk<TransactionRepository>()
    private val walletUserRepository = mockk<WalletUserRepository>()
    private val walletService = WalletService(transactionRepository = transactionRepository, walletUserRepository = walletUserRepository)

    private val userId = UUID.randomUUID()
    private val receiverId = UUID.randomUUID()
    private val receiver = mockk<WalletUser> {
        every { firstName } returns "John"
        every { lastName } returns "Doe"
        every { imageUrl } returns "http://image.url"
    }

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

        assertThat(result).isEqualTo(0.0)
    }

    @Test
    fun `validatePaymentAmount returns valid result for sufficient balance`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.of(receiver)
        every { transactionRepository.sumAmountByReceiverId(userId) } returns 100.0
        every { transactionRepository.sumAmountBySenderId(userId) } returns 20.0

        val result = walletService.validatePaymentAmount(userId, 50.0, receiverId)

        assert(result.isValid)
        assert(result.receiverName == "John Doe")
        assert(result.receiverImageUrl == "http://image.url")
    }

    @Test
    fun `validatePaymentAmount returns invalid for insufficient balance`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.of(receiver)
        every { transactionRepository.sumAmountByReceiverId(userId) } returns 30.0
        every { transactionRepository.sumAmountBySenderId(userId) } returns 20.0

        val result = walletService.validatePaymentAmount(userId, 20.0, receiverId)

        assert(!result.isValid)
    }

    @Test
    fun `validatePaymentAmount returns invalid for zero or negative amount`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.of(receiver)
        every { transactionRepository.sumAmountByReceiverId(userId) } returns 100.0
        every { transactionRepository.sumAmountBySenderId(userId) } returns 10.0

        val resultZero = walletService.validatePaymentAmount(userId, 0.0, receiverId)
        val resultNegative = walletService.validatePaymentAmount(userId, -5.0, receiverId)

        assert(!resultZero.isValid)
        assert(!resultNegative.isValid)
    }

    @Test
    fun `validatePaymentAmount throws when receiver not found`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.empty()

        assertThrows(IllegalArgumentException::class.java) {
            walletService.validatePaymentAmount(userId, 10.0, receiverId)
        }
    }

    companion object {
        private val USER_ID = UUID.fromString("a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6")
        private val RECEIVED =32543.0
        private val SENT = 3236.0
        private val EXPECTED_BALANCE = RECEIVED - SENT
    }
}