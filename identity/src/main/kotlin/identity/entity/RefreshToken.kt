package identity.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
data class RefreshToken(
    @Id
    @GeneratedValue
    val id: Long = 0,
    val refreshToken: String,
    val expiresIn: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User
)