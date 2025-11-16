package net.thechance.trends.entity

import jakarta.persistence.*
import java.util.*

@Table(name = "users", schema = "trends")
@Entity
data class TrendUser(
    @Id
    @Column(name = "user_id", columnDefinition = "uuid", nullable = false, updatable = false)
    val userId: UUID,

    @Column(name = "phone_number", nullable = false, unique = true)
    val phoneNumber: String,

    @Column(name = "first_name", nullable = false)
    val firstName: String,

    @Column(name = "last_name", nullable = false)
    val lastName: String,

    @Column(name = "username", nullable = false, unique = true)
    val username: String,

    @Column(name = "image_url", nullable = true, length = 2083)
    val imageUrl: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: Status
) {
    enum class Status {
        ACTIVE,
        BLOCKED
    }
}