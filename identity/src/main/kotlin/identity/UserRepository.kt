package net.thechance.identity

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {
    fun findByPhoneNumber(phoneNumber: String): User?
}