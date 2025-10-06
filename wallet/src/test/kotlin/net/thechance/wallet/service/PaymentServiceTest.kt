package net.thechance.wallet.service

import io.mockk.every
import io.mockk.mockk
import net.thechance.wallet.entity.user.WalletUser
import net.thechance.wallet.repository.WalletUserRepository
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.*

class PaymentServiceTest {

    private val walletService: WalletService = mockk<WalletService>()
    private val walletUserRepository: WalletUserRepository = mockk<WalletUserRepository>()

    private val paymentService = PaymentService(walletService, walletUserRepository)

    private val userId = UUID.randomUUID()
    private val receiverId = UUID.randomUUID()
    private val receiver = mockk<WalletUser> {
        every { firstName } returns "John"
        every { lastName } returns "Doe"
        every { imageUrl } returns "http://image.url"
    }

    @Test
    fun `validatePaymentAmount returns valid result for sufficient balance`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.of(receiver)
        every { walletService.getUserBalance(userId) } returns 100.0

        val result = paymentService.validatePaymentAmount(userId, 50.0, receiverId)

        assert(result.isValid)
        assert(result.receiverName == "John Doe")
        assert(result.receiverImageUrl == "http://image.url")
    }

    @Test
    fun `validatePaymentAmount returns invalid for insufficient balance`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.of(receiver)
        every { walletService.getUserBalance(userId) } returns 10.0

        val result = paymentService.validatePaymentAmount(userId, 20.0, receiverId)

        assert(!result.isValid)
    }

    @Test
    fun `validatePaymentAmount returns invalid for zero or negative amount`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.of(receiver)
        every { walletService.getUserBalance(userId) } returns 100.0
        every { walletService.getUserBalance(userId) } returns 10.0

        val resultZero = paymentService.validatePaymentAmount(userId, 0.0, receiverId)
        val resultNegative = paymentService.validatePaymentAmount(userId, -5.0, receiverId)

        assert(!resultZero.isValid)
        assert(!resultNegative.isValid)
    }

    @Test
    fun `validatePaymentAmount throws when receiver not found`() {
        every { walletUserRepository.findById(receiverId) } returns Optional.empty()

        assertThrows(IllegalArgumentException::class.java) {
            paymentService.validatePaymentAmount(userId, 10.0, receiverId)
        }
    }
}