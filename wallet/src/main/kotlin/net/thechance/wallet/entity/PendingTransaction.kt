package net.thechance.wallet.entity

import jakarta.persistence.*
import net.thechance.wallet.entity.user.WalletUser
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "pending_transactions", schema = "wallet")
data class PendingTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: Transaction.Type,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: WalletUser,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: WalletUser,

    @Column(nullable = false, updatable = false)
    val amount: BigDecimal,

    )


fun PendingTransaction.toTransaction(
    block: Block,
    status: Transaction.Status
): Transaction {
    return Transaction(
        id = this.id,
        sender = this.sender,
        receiver = this.receiver,
        amount = this.amount,
        block = block,
        createdAt = this.createdAt,
        status = status,
        type = this.type
    )
}