package wallet.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, updatable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column(name = "sender__wallet_address", columnDefinition = "uuid", nullable = false, updatable = false)
    val senderWalletAddress: UUID,

    @Column(name = "receiver_wallet_address", columnDefinition = "uuid", nullable = false, updatable = false)
    val receiverWalletAddress: UUID,

    @Column(name = "sender_signature", columnDefinition = "TEXT", nullable = false, updatable = false)
    val senderSignature: String,

    @Column(nullable = false, updatable = false)
    val amount: BigDecimal
)
