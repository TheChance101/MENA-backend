package net.thechance.wallet.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "blocks")
data class Block(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "previous_block_hash", nullable = false, updatable = false)
    val previousBlockHash: String,

    @Column(nullable = false, updatable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)