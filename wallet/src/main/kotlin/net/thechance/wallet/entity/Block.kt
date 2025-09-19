package net.thechance.wallet.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "blocks", schema = "wallet")
data class Block(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, updatable = false)
    val previousBlockHash: String,

    @Column(nullable = false, updatable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)