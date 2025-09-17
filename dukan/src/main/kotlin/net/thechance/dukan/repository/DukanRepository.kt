package net.thechance.dukan.repository

import net.thechance.dukan.entity.Dukan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DukanRepository : JpaRepository<Dukan, UUID> {
    fun existsByName(name: String): Boolean
    fun existsByOwnerId(ownerId: UUID): Boolean
    fun findByOwnerId(ownerId: UUID): Dukan?
}