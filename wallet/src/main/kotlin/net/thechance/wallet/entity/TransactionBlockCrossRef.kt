package net.thechance.wallet.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "transaction_block_cross_ref", schema = "wallet")
@IdClass(TransactionBlockId::class)
data class TransactionBlockCrossRef(
    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    val transactionId: UUID,

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    val blockId: UUID
)

data class TransactionBlockId(
    val transactionId: UUID,
    val blockId: UUID
) : Serializable