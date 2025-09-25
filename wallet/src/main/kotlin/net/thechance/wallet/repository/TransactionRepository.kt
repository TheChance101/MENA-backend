package net.thechance.wallet.repository

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.repository.transaction.TransactionProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID> {
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.receiverId = :receiverId")
    fun sumAmountByReceiverId(@Param("receiverId") receiverId: UUID): Double?

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.senderId = :senderId")
    fun sumAmountBySenderId(@Param("senderId") senderId: UUID): Double?

    @Query(
        """
        SELECT new net.thechance.wallet.repository.transaction.TransactionProjection(
        t.id,
        t.createdAt,
        t.amount,
        t.status,
        t.senderId,
        s.phoneNumber,
        t.receiverId,
        CASE 
            WHEN d.id IS NOT NULL THEN d.name
            ELSE r.phoneNumber
        END AS receiverName,
        CASE
            WHEN d.id IS NOT NULL THEN 'ONLINE_PURCHASE'
            WHEN t.senderId = :currentUserId THEN 'SENT'
            WHEN t.receiverId = :currentUserId THEN 'RECEIVED'
        END
    )
        FROM Transaction t
        JOIN User s ON t.senderId = s.id
        LEFT JOIN User r ON t.receiverId = r.id
        LEFT JOIN Dukan d ON t.receiverId = d.ownerId
          WHERE (:status IS NULL OR t.status = :status)
              AND (t.createdAt BETWEEN :startDate AND :endDate)
              AND (t.senderId = :currentUserId 
                   OR t.receiverId = :currentUserId
                   OR d.ownerId = :currentUserId)
              AND (
                :type IS NULL
                OR (:type = 'SENT' AND t.senderId = :currentUserId AND d.id IS NULL)
                OR (:type = 'RECEIVED' AND t.receiverId = :currentUserId AND d.id IS NULL)
                OR (:type = 'ONLINE_PURCHASE' AND d.id IS NOT NULL)
              )
        """
    )
    fun findFilteredTransactions(
        @Param("currentUserId") currentUserId: UUID,
        @Param("type") type: String?,
        @Param("status") status: Transaction.Status?,
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?,
        pageable: Pageable
    ): Page<TransactionProjection>


    @Query(
        """
        SELECT t FROM Transaction t
        WHERE t.senderId = :userId OR t.receiverId = :userId
        ORDER BY t.createdAt ASC
        LIMIT 1
        """
    )
    fun findFirstByUserId(@Param("userId") userId: UUID): Transaction?
}