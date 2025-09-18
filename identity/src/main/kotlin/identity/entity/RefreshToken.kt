package net.thechance.identity.entity

import jakarta.persistence.*

@Entity
@Table(name = "refresh_token", schema = "identity")
class RefreshToken(
    @Id
    @GeneratedValue
    val id: Long = 0,
    @Column(name = "refresh_token", nullable = false)
    val refreshToken: String,
    @Column(name = "expires_in", nullable = false)
    val expiresIn: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)