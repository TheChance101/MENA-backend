package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.wallet.repository.TransactionRepository
import org.junit.Test
import java.math.BigDecimal
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

        assertThat(result).isEqualTo(0.0)
    }

    companion object {
        private val USER_ID = UUID.fromString("a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6")
        private val RECEIVED =32543.0
        private val SENT = 3236.0
        private val EXPECTED_BALANCE = RECEIVED - SENT
    }
}