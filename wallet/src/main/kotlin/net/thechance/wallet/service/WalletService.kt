package net.thechance.wallet.service

import net.thechance.wallet.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class WalletService(
    private val transactionRepository: TransactionRepository
) {

    fun getUserBalance(userId: UUID): Double {

        val totalReceived = transactionRepository.sumAmountByReceiverId(userId) ?: 0.0

        val totalSent = transactionRepository.sumAmountBySenderId(userId) ?: 0.0

        return totalReceived - totalSent
    }
}