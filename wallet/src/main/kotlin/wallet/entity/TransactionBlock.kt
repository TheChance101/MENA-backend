package wallet.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "transaction_block")
@IdClass(TransactionBlockId::class)
data class TransactionBlock(
    @Id
    @Column(columnDefinition = "uuid", name = "transaction_id", nullable = false)
    val transactionId: UUID,

    @Id
    @Column(columnDefinition = "uuid", name = "block_id", nullable = false)
    val blockId: UUID
)

data class TransactionBlockId(
    val transactionId: UUID,
    val blockId: UUID
) : Serializable