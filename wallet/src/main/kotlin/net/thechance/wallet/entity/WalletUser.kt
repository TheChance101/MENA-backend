package net.thechance.wallet.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users", schema = "wallet")
data class WalletUser(
    @Id
    val userId: UUID,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = true)
    val imageUrl: String?,

    @Column(nullable = false, unique = true)
    val phoneNumber: String,

    @Column(nullable = false)
    val isDeleted: Boolean,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "dukan_id", referencedColumnName = "dukanId")
    val dukan: WalletDukan? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: Status
) {
    val userName: String
        get() = "$firstName $lastName"

    enum class Status {
        ACTIVE,
        BLOCKED
    }

    fun isBlocked(): Boolean = status == Status.BLOCKED
}