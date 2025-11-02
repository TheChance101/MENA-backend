package net.thechance.identity.repository

import net.thechance.identity.entity.AdminUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AdminUserRepository : JpaRepository<AdminUser, UUID> {
    fun findByUsername(username: String): AdminUser?
}