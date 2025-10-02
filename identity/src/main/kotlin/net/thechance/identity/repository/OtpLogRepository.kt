package net.thechance.identity.repository

import jakarta.transaction.Transactional
import net.thechance.identity.entity.OtpLog
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface OtpLogRepository: JpaRepository<OtpLog, UUID> {
    fun findByPhoneNumberOrderByCreatedAtDesc(phoneNumber: String, pageable: Pageable): List<OtpLog>

    fun findByOtpAndSessionId(otp: String, sessionId: UUID): OtpLog?

    fun findFirstBySessionIdOrderByCreatedAtDesc(sessionId: UUID): OtpLog?

    @Modifying
    @Transactional
    @Query("""
        UPDATE OtpLog o 
        SET o.expireAt = :newExpireAt 
        WHERE o.phoneNumber = :phoneNumber
        AND o.expireAt > :newExpireAt
    """)
    fun updateExpirationByPhoneNumber(
        @Param("phoneNumber") phoneNumber: String,
        @Param("newExpireAt") newExpireAt: Instant = Instant.now().minusSeconds(1)
    )

    @Modifying
    @Transactional
    @Query("""
        UPDATE OtpLog o 
        SET o.isVerified = true
        WHERE o.sessionId = :sessionId
    """)
    fun verifyOtp(
        @Param("sessionId") sessionId: UUID
    )

    @Modifying
    @Transactional
    @Query("""
        UPDATE OtpLog o 
        SET o.expireAt = :now
        WHERE o.sessionId = :sessionId
    """)
    fun expireLatestOtpBySessionId(
        @Param("sessionId") sessionId: UUID,
        @Param("now") now: Instant = Instant.now()
    )

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM OtpLog o 
        WHERE o.expireAt < :now
    """)
    fun deleteExpiredOtpBefore(
        @Param("now") now: Instant
    )
}