package net.thechance.trends.repository

import net.thechance.trends.entity.Reel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReelsRepository: JpaRepository<Reel, UUID>{
    fun existsByIdAndOwnerId(id: UUID, ownerId: UUID): Boolean
}