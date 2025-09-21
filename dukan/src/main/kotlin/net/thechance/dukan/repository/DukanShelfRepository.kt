package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanShlef
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DukanShelfRepository : JpaRepository<DukanShlef, UUID>