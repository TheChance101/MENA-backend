package net.thechance.identity.repository

import net.thechance.identity.entity.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface AddressRepository : JpaRepository<Address, UUID> {
    fun findByIdAndUserId(id: UUID, userId: UUID): Address?
    fun findByUserIdOrderByCreatedAtAsc(userId: UUID): List<Address>
    fun existsByUserId(userId: UUID): Boolean

    fun existsByIdAndUserIdAndIsActive(id: UUID, userId: UUID, isActive: Boolean): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isActive = FALSE WHERE a.userId = :userId AND a.isActive = TRUE")
    fun deactivateActiveAddressForUser(@Param("userId") userId: UUID): Int
}