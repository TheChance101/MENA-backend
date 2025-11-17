package net.thechance.trends.repository

import net.thechance.trends.entity.TrendUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface TrendUserRepository : JpaRepository<TrendUser, UUID> {
    @Modifying
    @Query("UPDATE TrendUser u SET u.status = :status WHERE u.userId = :userId")
    fun updateUserStatus(@Param("userId") userId: UUID, @Param("status") status: TrendUser.Status): Int
}