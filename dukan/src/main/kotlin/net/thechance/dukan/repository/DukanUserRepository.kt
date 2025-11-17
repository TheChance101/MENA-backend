package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DukanUserRepository: JpaRepository<DukanUser, UUID> {
}