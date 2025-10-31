package net.thechance.identity.service

import net.thechance.identity.entity.AdminRefreshToken
import net.thechance.identity.entity.AdminUser
import net.thechance.identity.repository.AdminRefreshTokenRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class AdminRefreshTokenService(
    private val adminRefreshTokenRepository: AdminRefreshTokenRepository
) {
    fun createRefreshToken(adminUser: AdminUser): AdminRefreshToken {
        val expiry = Instant.now().plus(Duration.ofDays(EXPIRATION_DAYS)).epochSecond
        val token = UUID.randomUUID().toString()
        val refreshToken = AdminRefreshToken(refreshToken = token, expiresIn = expiry, adminUser = adminUser)
        return adminRefreshTokenRepository.save(refreshToken)
    }

    fun validateRefreshToken(token: String): AdminRefreshToken? {
        val stored = adminRefreshTokenRepository.findByRefreshToken(token) ?: return null
        val expiryDate = Instant.ofEpochSecond(stored.expiresIn)
        return stored.takeIf { expiryDate.isAfter(Instant.now()) } ?: run {
            adminRefreshTokenRepository.delete(stored)
            null
        }
    }

    companion object {
        const val EXPIRATION_DAYS = 7L
    }
}

