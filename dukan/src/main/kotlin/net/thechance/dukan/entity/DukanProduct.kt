package net.thechance.dukan.entity

import jakarta.persistence.*
import java.math.BigDecimal
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

    @Embedded
    val price: Price,

    @Column(name = "discount", precision = 5, scale = 2)
    val discount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String,

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
    val favorites: MutableSet<FavoriteProduct> = emptySet()
)
