package net.thechance.identity.service

import net.thechance.identity.entity.User
import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.UnauthorizedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChangePasswordService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun changePassword(userId: UUID, currentPassword: String, newPassword: String, confirmPassword: String) {
        validateRequest(newPassword, confirmPassword)
        findAndVerifyUser(userId, currentPassword)
            .run { updateUserWithNewPassword(this, newPassword) }
            .also { updatedUser -> userRepository.save(updatedUser) }
    }

    private fun validateRequest(newPassword: String, confirmPassword: String) {
        (newPassword == confirmPassword).takeIf { it } ?: throw PasswordMismatchException()
    }

    private fun findAndVerifyUser(userId: UUID, currentRawPassword: String): User {
        return userRepository.findByIdOrNull(userId)
            .takeIf { it != null }
            ?.also { user ->
                passwordEncoder.matches(currentRawPassword, user.password)
                    .takeIf { it } ?: throw UnauthorizedException()
            }
            ?: throw UserNotFoundException("User not found")
    }

    private fun updateUserWithNewPassword(user: User, newRawPassword: String): User {
        return passwordEncoder.encode(newRawPassword).let { encodedNewPassword ->
            user.copy(password = encodedNewPassword)
        }
    }
}