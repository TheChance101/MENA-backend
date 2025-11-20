package net.thechance.wallet.scheduler

import net.thechance.wallet.service.TransactionService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PendingTransactionScheduler (
    private val transactionsService: TransactionService
){
    @Scheduled(cron = "0 0 0 * * *")
    fun clearExpiredPendingTransactions(){
        val expirationTime = LocalDateTime.now().minusDays(1)
        transactionsService.clearExpiredPendingTransactions(expirationTime)
    }
}