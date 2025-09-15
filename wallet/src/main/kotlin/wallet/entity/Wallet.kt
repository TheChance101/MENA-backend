package net.thechance.wallet.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "wallets")
data class Wallet(
    @Id
    @Column(name = "address", updatable = false, nullable = false)
    val address: String,

    @Column(name = "user_id", columnDefinition = "uuid", updatable = false, nullable = false)
    val userId: UUID
)