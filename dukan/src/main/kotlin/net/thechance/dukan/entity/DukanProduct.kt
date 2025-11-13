package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*
import java.util.Collections.emptySet

@Table(
    name = "dukan_products",
    schema = "dukan",
    uniqueConstraints = [UniqueConstraint(columnNames = ["dukan_id", "name"])]
)
@Entity
data class DukanProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    val name: String,

    @ManyToOne
    @JoinColumn(name = "shelf_id", nullable = false)
    val shelf: DukanShelf,

    @ManyToOne
    @JoinColumn(name = "dukan_id", nullable = false)
    val dukan: Dukan,

    @Column(name = "price", nullable = false)
    val price: Double,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String,

    @Column(name = "is_deleted")
    val isDeleted: Boolean= false,

    @ElementCollection
    @CollectionTable(
        name = "product_images",
        schema = "dukan",
        joinColumns = [JoinColumn(name = "product_id")]
    )
    @Column(name = "image_url", nullable = false)
    val imageUrls: List<String>,

    @Transient
    var tempQuantity: Int = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    val favorites: MutableSet<FavoriteProduct> = emptySet(),
    @Column(name = "is_out_of_stock", nullable = false)
    val isOutOfStock: Boolean = false,
)
