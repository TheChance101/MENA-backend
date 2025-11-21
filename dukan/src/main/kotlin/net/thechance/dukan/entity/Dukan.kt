package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*
import java.util.Collections.emptySet

@Table(name = "dukans", schema = "dukan")
@Entity
data class Dukan(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "owner_id", nullable = false, unique = true)
    val ownerId: UUID,
    @Column(name = "name", nullable = false, unique = true)
    val name: String,
    @ManyToMany
    @JoinTable(
        name = "dukan_categories_junction",
        joinColumns = [JoinColumn(name = "dukan_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
        schema = "dukan"
    )
    val categories: Set<DukanCategory>,
    @Column(name = "image_url", nullable = true)
    val imageUrl: String? = null,
    @Column(name = "address", nullable = false)
    val address: String,
    @Column(name = "latitude", nullable = false)
    val latitude: Double,
    @Column(name = "longitude", nullable = false)
    val longitude: Double,
    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    val color: DukanColor,
    @Enumerated(EnumType.STRING)
    @Column(name = "style", nullable = false)
    val style: Style,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: Status = Status.PENDING,
    @Enumerated(EnumType.STRING)
    @Column(name = "activation_status", nullable = false)
    val activationStatus: ActivationStatus,
    @OneToMany(mappedBy = "dukan", cascade = [CascadeType.ALL])
    val shelves: Set<DukanShelf> = emptySet(),
    @Column(name = "createdAt")
    val createdAt: Instant = Instant.now(),
    @OneToMany(mappedBy = "dukan", fetch = FetchType.LAZY)
    val favorites: Set<FavoriteDukan> = emptySet()
) {
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

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE,
    }
}