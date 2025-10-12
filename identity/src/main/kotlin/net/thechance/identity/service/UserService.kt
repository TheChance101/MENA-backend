package net.thechance.identity.service

import net.thechance.identity.api.dto.UpdateProfileRequest
import net.thechance.identity.entity.User
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun findByPhoneNumber(phoneNumber: String): User {
        return userRepository.findByPhoneNumber(phoneNumber) ?: throw InvalidCredentialsException("User not found")
    }

    fun findById(userId: UUID): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("User with id: $userId not found")
    }

    fun userExists(userId: UUID): Boolean {
        return userRepository.existsById(userId)
    }

    fun updatePasswordByPhoneNumber(phoneNumber: String, newPassword: String) {
        val userWithNewPassword = getUserWithNewPassword(phoneNumber, newPassword)
        try {
            val savedUser = userRepository.save(userWithNewPassword)
            if (savedUser.password != newPassword) throw PasswordNotUpdatedException()
        } catch (exception: Exception) {
            throw PasswordNotUpdatedException()
        }
    }

    private fun getUserWithNewPassword(phoneNumber: String, newPassword: String): User {
        val user = findByPhoneNumber(phoneNumber)
        return user.copy(password = newPassword)
    }

    fun updateUserProfile(userId: UUID, updateProfileRequest: UpdateProfileRequest): User {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User with id: $userId not found") }

        val updatedUser = user.copy(
            username = updateProfileRequest.username,
            firstName = updateProfileRequest.firstName,
            lastName = updateProfileRequest.lastName,
            imageUrl = updateProfileRequest.imageUrl,
            birthDate = LocalDate.parse(updateProfileRequest.birthDate),
            gender = updateProfileRequest.gender,
        )
        return userRepository.save(updatedUser)
    }
}