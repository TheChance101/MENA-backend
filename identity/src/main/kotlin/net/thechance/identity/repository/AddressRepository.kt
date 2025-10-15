package net.thechance.identity.repository

import net.thechance.identity.entity.Address
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AddressRepository : JpaRepository<Address, UUID> {
    fun findByIdAndUserId(id: UUID, userId: UUID): Address?

    fun findByIsActiveAndUserId(isActive: Boolean, userId: UUID): Address?

    fun findByUserIdOrderByCreatedAtAsc(userId: UUID): List<Address>
}