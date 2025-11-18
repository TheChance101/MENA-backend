package net.thechance.identity.repository

import net.thechance.identity.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {
    fun findByPhoneNumberAndIsDeletedFalse(phoneNumber: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByPhoneNumberAndIsDeletedFalse(phoneNumber: String): Boolean

    @Query("""
    SELECT u FROM User u
    WHERE 
        LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
        OR u.phoneNumber LIKE CONCAT('%', :query, '%')
""")
    fun findByFullNameOrPhoneNumber(
        @Param("query") query: String,
        pageable: Pageable
    ): Page<User>

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :time WHERE u.id = :userId")
    fun updateLastLoginTime(
        @Param("userId") userId: UUID,
        @Param("time") time: LocalDateTime
    ): Int

    @Modifying
    @Query("UPDATE User u SET u.lastVisitAt = :time WHERE u.id = :userId")
    fun updateLastVisitTime(
        @Param("userId") userId: UUID,
        @Param("time") time: LocalDateTime
    ): Int

    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    fun updateStatus(
        @Param("userId") userId: UUID,
        @Param("status") status: User.Status
    ): Int

    fun existsByIdAndIsDeletedFalse(userId: UUID): Boolean
}