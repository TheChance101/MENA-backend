package net.thechance.wallet.entity

import jakarta.persistence.*
import net.thechance.events.wallet.TransactionCompletedEvent
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "transactions", schema = "wallet")
data class Transaction(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: Type,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: WalletUser,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: WalletUser,

    @Column(nullable = false, updatable = false)
    val amount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false, updatable = false)
    val block: Block
){

    enum class Status{
        FAILED,
        SUCCESS,
        PENDING
    }

    enum class Type {
        P2P,
        ONLINE_PURCHASE
    }
}

fun Transaction.toTransactionCompletedEvent(): TransactionCompletedEvent {
    return TransactionCompletedEvent(
        transactionId = this.id,
        createdAt = this.createdAt,
        status = when (this.status) {
            Transaction.Status.FAILED -> TransactionCompletedEvent.TransactionStatus.FAILED
            Transaction.Status.SUCCESS -> TransactionCompletedEvent.TransactionStatus.SUCCESS
            Transaction.Status.PENDING -> TransactionCompletedEvent.TransactionStatus.PENDING
        },
        type = when (this.type) {
            Transaction.Type.P2P -> TransactionCompletedEvent.TransactionType.P2P
            Transaction.Type.ONLINE_PURCHASE -> TransactionCompletedEvent.TransactionType.ONLINE_PURCHASE
        },
        senderId = this.sender.userId,
        receiverId = this.receiver.userId,
        amount = this.amount
    )
}
