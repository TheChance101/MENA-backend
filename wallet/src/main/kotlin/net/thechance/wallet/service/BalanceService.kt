package net.thechance.wallet.service

import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.utils.orZero
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BalanceService(
    private val transactionRepository: TransactionRepository
) {

    fun getUserBalance(userId: UUID, startDate: LocalDateTime? = null, endDate: LocalDateTime? = null): Double {
        val totalReceived = transactionRepository.sumAmountByReceiverId(userId, startDate, endDate).orZero()
        val totalSent = transactionRepository.sumAmountBySenderId(userId, startDate, endDate).orZero()
        return totalReceived - totalSent
    }
}