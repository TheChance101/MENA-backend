package net.thechance.wallet.repository

import net.thechance.wallet.entity.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID> {
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.receiver.userId = :receiverId")
    fun sumAmountByReceiverId(@Param("receiverId") receiverId: UUID): Double?

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sender.userId = :senderId")
    fun sumAmountBySenderId(@Param("senderId") senderId: UUID): Double?

    @Query(
        """
        SELECT t FROM Transaction t
        WHERE (t.sender.userId = :currentUserId OR t.receiver.userId = :currentUserId)
          AND (:status IS NULL OR t.status = :status)
          AND t.createdAt BETWEEN :startDate AND :endDate
          AND (
                :transactionTypes IS NULL
            OR (
                ('SENT' IN :transactionTypes AND t.type = 'P2P' AND t.sender.userId = :currentUserId)
                OR ('RECEIVED' IN :transactionTypes AND t.type = 'P2P' AND t.receiver.userId = :currentUserId)
                OR ('ONLINE_PURCHASE' IN :transactionTypes AND t.type = 'ONLINE_PURCHASE')
            )
        )
     """
    )
    fun findFilteredTransactions(
        @Param("currentUserId") currentUserId: UUID,
        @Param("status") status: Transaction.Status?,
        @Param("transactionType") transactionTypes: List<String>?,
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?,
        pageable: Pageable
    ): Page<Transaction>


    fun findFirstBySender_UserIdOrReceiver_UserIdOrderByCreatedAtAsc(
        senderId: UUID,
        receiverId: UUID
    ): Transaction?
}