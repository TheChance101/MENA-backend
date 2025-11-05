package net.thechance.identity.repository

import net.thechance.identity.entity.AdminRefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AdminRefreshTokenRepository : JpaRepository<AdminRefreshToken, Long> {
    fun findByRefreshToken(refreshToken: String): AdminRefreshToken?
    fun removeByAdminUserId(adminUserId: UUID)
}