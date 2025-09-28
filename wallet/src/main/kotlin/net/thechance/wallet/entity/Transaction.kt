package net.thechance.wallet.entity

import jakarta.persistence.*
import net.thechance.wallet.entity.user.WalletUser
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "transactions", schema = "wallet")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: Type,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: WalletUser,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: WalletUser,

    @Column(columnDefinition = "TEXT", nullable = false, updatable = false)
    val senderSignature: String,

    @Column(nullable = false, updatable = false)
    val amount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false, updatable = false)
    val block: Block
){

    enum class Status{
        FAILED,
        SUCCESS
    }

    enum class Type {
        P2P,
        ONLINE_PURCHASE
    }
}
