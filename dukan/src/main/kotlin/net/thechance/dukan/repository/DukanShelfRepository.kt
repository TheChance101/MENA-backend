package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanShelf
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DukanShelfRepository : JpaRepository<DukanShelf, UUID>