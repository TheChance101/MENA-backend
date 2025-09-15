package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanColor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DukanColorRepository: JpaRepository<DukanColor, UUID>