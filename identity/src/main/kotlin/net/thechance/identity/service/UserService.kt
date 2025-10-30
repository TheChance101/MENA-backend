package net.thechance.identity.service

import net.thechance.identity.entity.User
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.model.UserServiceModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

private typealias ImageUri = String

@Service
class UserService(
    private val userRepository: UserRepository,
    private val identityImageStorageService: IdentityImageStorageService,
    @param:Value("\${identity.resources.profile-image-directory}") private val profileImageDirectory: String
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
        } catch (_: Exception) {
            throw PasswordNotUpdatedException()
        }
    }

    private fun getUserWithNewPassword(phoneNumber: String, newPassword: String): User {
        val user = findByPhoneNumber(phoneNumber)
        return user.copy(password = newPassword)
    }

    fun updateUserProfile(user: UserServiceModel): User {
        val userEntity = findById(user.id)
        val updatedUser = userEntity.copy(
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            gender = user.gender
        )

        return userRepository.save(updatedUser)
    }

    fun updateUserImage(
        userId: UUID,
        imageFile: MultipartFile
    ): ImageUri {
        val user = findById(userId)
        val newImageUrl = identityImageStorageService.uploadImage(
            file = imageFile,
            fileName = "${user.id}",
            folderName = profileImageDirectory
        )
        val updatedUser = user.copy(imageUrl = newImageUrl)
        userRepository.save(updatedUser)
        return newImageUrl
    }

    fun deleteUserImage(userId: UUID) {
        val user = findById(userId)
        user.imageUrl?.let { imageUrl ->
            identityImageStorageService.deleteImage(imageUrl)
            userRepository.save(user.copy(imageUrl = null))
        }
    }
}