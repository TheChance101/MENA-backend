package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "carts", schema = "dukan")
@Entity
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = true)
    val userId: UUID,

    @ManyToOne
    @JoinColumn(name = "dukan_id", nullable = false)
    val dukan: Dukan,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableSet<CartItem> = mutableSetOf(),

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Cart
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}