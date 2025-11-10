package net.thechance.identity.repository

import net.thechance.identity.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenRepository: JpaRepository<RefreshToken, Long> {
    fun findByRefreshToken(refreshToken: String): RefreshToken?
    fun removeByUserId(userId: UUID)
}