package net.thechance.identity.service

import net.thechance.identity.entity.User
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.model.UserServiceModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val identityImageStorageService: IdentityImageStorageService
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

    fun updateUserProfile(
        user: UserServiceModel,
        shouldUpdatedImage: Boolean,
        file: MultipartFile?
    ): User {
        val userEntity = userRepository.findById(user.id)
            .orElseThrow { UserNotFoundException("User with id: ${user.id} not found") }

        val imageUrl = when {
            !shouldUpdatedImage -> userEntity.imageUrl
            file != null -> identityImageStorageService.uploadImage(file = file, fileName = "${userEntity.id}")
            else -> ""
        }

        val updatedUser = userEntity.copy(
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            imageUrl = imageUrl,
            birthDate = LocalDate.parse(user.birthDate),
            gender = user.gender,
        )

        return userRepository.save(updatedUser)
    }
}