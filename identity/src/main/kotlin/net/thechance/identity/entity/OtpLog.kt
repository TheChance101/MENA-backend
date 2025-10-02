package net.thechance.identity.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "otp_logs", schema = "identity")
class OtpLog(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,
    @Column(name = "otp", nullable = false, length = 6)
    val otp: String,
    @Column(name = "is_verified", nullable = false)
    val isVerified: Boolean = false,
    @Column(name = "session_id", nullable = false)
    val sessionId: UUID = UUID.randomUUID(),
    @Column(name = "expire_at", nullable = false)
    val expireAt: Instant,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)