package net.thechance.dukan.repository

import net.thechance.dukan.entity.Shelf
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ShelfRepository : JpaRepository<Shelf, UUID>