package net.thechance.wallet.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "sender_address", columnDefinition = "uuid", nullable = false, updatable = false)
    val senderAddress: String,

    @Column(name = "receiver_address", columnDefinition = "uuid", nullable = false, updatable = false)
    val receiverAddress: String,

    @Column(name = "sender_signature", columnDefinition = "TEXT", nullable = false, updatable = false)
    val senderSignature: String,

    @Column(nullable = false, updatable = false)
    val amount: BigDecimal
)
