package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "users", schema = "dukan")
data class DukanUser(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val userId: UUID,

    @Column(name = "first_name", nullable = false)
    val firstName: String,

    @Column(name = "last_name", nullable = false)
    val lastName: String,

    @Column(name = "image_url", nullable = true)
    val imageUrl: String?,

    @Column(name = "phone_number", nullable = false, unique = true)
    val phoneNumber: String,

    @Column(name = "address", nullable = false)
    var address: String = "",

    @Column(name = "latitude", nullable = false)
    var latitude: Double = 0.0,

    @Column(name = "longitude", nullable = false)
    var longitude: Double = 0.0,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "dukanId", referencedColumnName = "id")
    val dukan: Dukan? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
) {
    val userName: String
        get() = "$firstName $lastName"
}