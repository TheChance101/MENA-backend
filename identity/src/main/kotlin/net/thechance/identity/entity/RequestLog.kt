package net.thechance.identity.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.util.*

@Entity
@Table(name = "request_log", schema = "identity")
class RequestLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ColumnDefault("now()")
    @Column(name = "request_time", nullable = false, updatable = false)
    val requestTime: Instant = Instant.now(),

    @Column(name = "ip_address", nullable = false, length = Integer.MAX_VALUE)
    val ipAddress: String,

    @Column(name = "url", nullable = false)
    val url: String
)