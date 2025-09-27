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

    fun findByPhoneNumberAndOtpAndSessionId(phoneNumber: String, otp: String, sessionId: UUID): OtpLog?

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
    @Query("""
        UPDATE OtpLog o 
        SET o.isVerified = true 
        WHERE o.phoneNumber = :phoneNumber
        AND o.sessionId = :sessionId
    """)
    fun verifyOtp(
        @Param("phoneNumber") phoneNumber: String,
        @Param("sessionId") sessionId: UUID
    )
}