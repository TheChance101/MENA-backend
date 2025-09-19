package net.thechance.identity.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.util.*

@Entity
@Table(name = "login_log", schema = "identity")
class LoginLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,

    @Column(name = "is_success", nullable = false, updatable = false)
    val isSuccess: Boolean,

    @ColumnDefault("now()")
    @Column(name = "login_time", nullable = false, updatable = false)
    val loginTime: Instant = Instant.now(),

    @Column(name = "ip_address", nullable = false, length = Integer.MAX_VALUE)
    val ipAddress: String
)