package net.thechance.dukan.repository

import net.thechance.dukan.entity.Dukan
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DukanRepository: JpaRepository<Dukan, UUID>