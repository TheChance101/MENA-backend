package net.thechance.wallet.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.wallet.repository.TransactionRepository
import org.junit.Test
import java.util.*


class WalletServiceTest {
    private val transactionRepository = mockk<TransactionRepository>()
    private val walletService = WalletService(transactionRepository = transactionRepository)

    @Test
    fun `getUserBalance should total return user balance when it is called`() {
        every { transactionRepository.sumAmountBySenderId(USER_ID) } returns SENT
        every { transactionRepository.sumAmountByReceiverId(USER_ID) } returns RECEIVED

        val result = walletService.getUserBalance(USER_ID)

        assertThat(result).isEqualTo(RECEIVED - SENT)
    }

    @Test
    fun `getUserBalance should return 0 when no transactions`() {
        every { transactionRepository.sumAmountBySenderId(USER_ID) } returns null
        every { transactionRepository.sumAmountByReceiverId(USER_ID) } returns null

        val result = walletService.getUserBalance(USER_ID)

        assertThat(result).isEqualTo(0.0)
    }

    companion object {
        private val USER_ID = UUID.randomUUID()
        private const val RECEIVED = 32543.0
        private const val SENT = 3236.0
    }
}