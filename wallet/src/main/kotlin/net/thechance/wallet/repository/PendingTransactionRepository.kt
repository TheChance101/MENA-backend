package net.thechance.wallet.repository

import net.thechance.wallet.entity.PendingTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.time.LocalDateTime
import java.util.*

interface PendingTransactionRepository : JpaRepository<PendingTransaction, UUID> {
    @Modifying
    fun deleteAllByCreatedAtBefore(expirationTime: LocalDateTime): Int
}