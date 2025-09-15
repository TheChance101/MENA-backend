package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanCategory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CategoryRepository : JpaRepository<DukanCategory, UUID>