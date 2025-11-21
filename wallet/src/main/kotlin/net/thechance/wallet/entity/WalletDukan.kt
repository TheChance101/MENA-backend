package net.thechance.wallet.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "dukans", schema = "wallet")
data class WalletDukan(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val dukanId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val imageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: Status = Status.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "activation_status", nullable = true)
    val activationStatus: ActivationStatus,
){
    enum class Status {
        APPROVED,
        REJECTED,
        PENDING,
    }

    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED,
        ONHOLD
    }
}