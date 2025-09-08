package net.thechance.identity

import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class RefreshTokenService(
    val refreshTokenRepository: RefreshTokenRepository
) {
    fun createRefreshToken(user: User): RefreshToken {
        val expiry = Instant.now().plus(Duration.ofDays(7)).epochSecond
        val token = UUID.randomUUID().toString()
        val refreshToken = RefreshToken(refreshToken = token, expiresIn = expiry, user = user)
        return refreshTokenRepository.save(refreshToken)
    }

    fun validateRefreshToken(token: String): RefreshToken? {
        val stored = refreshTokenRepository.findByRefreshToken(token) ?: return null
        val expiryDate = Instant.ofEpochSecond(stored.expiresIn)
        return if (expiryDate.isAfter(Instant.now())) stored else null
    }
}