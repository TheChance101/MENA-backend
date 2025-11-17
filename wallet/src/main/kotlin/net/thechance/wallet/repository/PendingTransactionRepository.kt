package net.thechance.wallet.repository

import net.thechance.wallet.entity.PendingTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

interface PendingTransactionRepository : JpaRepository<PendingTransaction, UUID> {
    @Modifying
    @Transactional
    fun deleteAllByCreatedAtBefore(expirationTime: LocalDateTime): Int
}