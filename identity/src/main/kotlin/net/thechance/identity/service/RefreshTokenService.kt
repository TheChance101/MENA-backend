package net.thechance.identity.service

import net.thechance.identity.entity.RefreshToken
import net.thechance.identity.entity.User
import net.thechance.identity.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.*

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

    @Transactional
    fun deleteUserRefreshTokens(userId: UUID) {
        refreshTokenRepository.removeByUserId(userId)
    }
}