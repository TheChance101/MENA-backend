package net.thechance.trends.repository

import net.thechance.trends.entity.Reel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReelsRepository : JpaRepository<Reel, UUID> {
    fun findByOwnerId(ownerId: UUID, pageable: Pageable): Page<Reel>
    fun existsByIdAndOwnerId(id: UUID, ownerId: UUID): Boolean
}