package net.thechance.wallet.entity

import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "public_keys", schema = "wallet")
data class PublicKey(
    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    val userId: UUID,

    @Column(columnDefinition = "TEXT", updatable = false, nullable = false)
    val publicKey: String
)
