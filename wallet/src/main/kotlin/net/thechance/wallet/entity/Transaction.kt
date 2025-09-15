package net.thechance.wallet.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "transactions", schema = "wallet")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    val senderId: UUID,

    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    val receiverId: UUID,

    @Column(columnDefinition = "TEXT", nullable = false, updatable = false)
    val senderSignature: String,

    @Column(nullable = false, updatable = false)
    val amount: BigDecimal,
)
