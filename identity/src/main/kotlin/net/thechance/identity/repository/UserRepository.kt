package net.thechance.identity.repository

import net.thechance.identity.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {
    fun findByPhoneNumber(phoneNumber: String): User?
}