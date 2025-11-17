package net.thechance.dukan.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users", schema = "dukan")
data class DukanUser(
    @Id
    val userId: UUID,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = true)
    val imageUrl: String?,

    @Column(nullable = false, unique = true)
    val phoneNumber: String,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "dukan_id", referencedColumnName = "dukanId")
    val dukan: Dukan? = null,

) {
    val userName: String
        get() = "$firstName $lastName"
}