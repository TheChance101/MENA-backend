package net.thechance.wallet.entity

import jakarta.persistence.*
import net.thechance.wallet.entity.user.WalletUser
import java.util.*

@Entity
@Table(name = "dukan", schema = "wallet")
data class WalletDukan(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val dukanId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val imageUrl: String? = null,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    val user: WalletUser
)