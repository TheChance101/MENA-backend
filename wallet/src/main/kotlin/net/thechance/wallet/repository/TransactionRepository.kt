package net.thechance.wallet.repository

import net.thechance.wallet.entity.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.util.UUID

interface TransactionRepository : JpaRepository<Transaction, UUID> {
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.receiverId = :receiverId")
    fun sumAmountByReceiverId(@Param("receiverId") receiverId: UUID): BigDecimal?

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.senderId = :senderId")
    fun sumAmountBySenderId(@Param("senderId") senderId: UUID): BigDecimal?

    fun findBySenderIdOrReceiverIdOrderByCreatedAtDesc(
        senderId: UUID,
        receiverId: UUID,
        pageable: Pageable
    ): Page<Transaction>
}