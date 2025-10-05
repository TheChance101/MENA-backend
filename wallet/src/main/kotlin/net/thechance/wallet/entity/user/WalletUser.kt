package net.thechance.wallet.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*


@Entity
@Table(name = "wallet_user", schema = "wallet")
data class WalletUser(
    @Id
    val userId: UUID,

    @Column(nullable = false)
    val userName: String,

    val dukanName: String? = null,

    @Column(nullable = false)
    val firstName: String,
    @Column(nullable = false)
    val lastName: String,
    @Column(nullable = true)
    val imageUrl: String?
)
