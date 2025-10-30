package net.thechance.wallet.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "dukans", schema = "wallet")
data class WalletDukan(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val dukanId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val imageUrl: String? = null
)