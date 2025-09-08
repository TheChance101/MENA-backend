package net.thechance.identity

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun findByPhoneNumber(phoneNumber: String): User {
        return userRepository.findByPhoneNumber(phoneNumber) ?: throw IllegalStateException("User not found")
    }

    fun userExists(userId: UUID): Boolean {
        return userRepository.existsById(userId)
    }
}