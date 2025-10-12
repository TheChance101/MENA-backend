package net.thechance.wallet.entity.user

import jakarta.persistence.*
import net.thechance.wallet.entity.WalletDukan
import java.util.*


@Entity
@Table(name = "wallet_user", schema = "wallet")
data class WalletUser(
    @Id
    val userId: UUID,

    @Column(nullable = false)
    val userName: String,

    @Column(nullable = false)
    val firstName: String,
    @Column(nullable = false)
    val lastName: String,
    @Column(nullable = true)
    val imageUrl: String?,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val dukan: WalletDukan? = null
)