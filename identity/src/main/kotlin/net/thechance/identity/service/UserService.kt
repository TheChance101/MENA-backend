package net.thechance.identity.service

import jakarta.transaction.Transactional
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.identity.entity.User
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.mapper.createUserUpdatedEvent
import net.thechance.identity.service.mapper.createUserUpdatedEventForUpdateImage
import net.thechance.identity.service.mapper.createUserUpdatedEventForUpdatePassword
import net.thechance.identity.service.mapper.toUserUpdatedEvent
import net.thechance.identity.service.model.UserServiceModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

private typealias ImageUri = String

@Service
class UserService(
    private val userRepository: UserRepository,
    private val identityImageStorageService: IdentityImageStorageService,
    @param:Value("\${identity.resources.profile-image-directory}") private val profileImageDirectory: String,
    private val eventPublisher: MenaEventPublisher
) {

    fun findByPhoneNumber(phoneNumber: String): User {
        return userRepository.findByPhoneNumber(phoneNumber) ?: throw UserNotFoundException("User not found")
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
        val savedUser = userRepository.save(userWithNewPassword)
        if (savedUser.password != newPassword) throw PasswordNotUpdatedException()
        eventPublisher.publish(createUserUpdatedEventForUpdatePassword(newPassword))
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
        val savedUser = userRepository.save(updatedUser)
        eventPublisher.publish(savedUser.toUserUpdatedEvent(oldUser = userEntity))
        return savedUser
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
        eventPublisher.publish(createUserUpdatedEventForUpdateImage(newImageUrl))
        return newImageUrl
    }

    fun deleteUserImage(userId: UUID) {
        val user = findById(userId)
        user.imageUrl?.let { imageUrl ->
            identityImageStorageService.deleteImage(
                fileName = imageUrl,
                folderName = profileImageDirectory
            )
            userRepository.save(user.copy(imageUrl = null))
            eventPublisher.publish(createUserUpdatedEventForUpdateImage(null))
        }
    }

    fun userExistsByUserName(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun userExistsByPhoneNumber(phoneNumber: String): Boolean {
        return userRepository.existsByPhoneNumber(phoneNumber)
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    fun findUsersByQuery(query: String, pageable: Pageable): Page<User> {
        return userRepository.findByFullNameOrPhoneNumber(query, pageable)
    }

    @Transactional
    fun updateUserLastLoginTime(userId: UUID, time: LocalDateTime) {
        val updatedUsersCount = userRepository.updateLastLoginTime(userId, time)
        if (updatedUsersCount == 0) throw UserNotFoundException("User with id: $userId not found")
    }

    @Transactional
    fun updateUserLastVisitTime(userId: UUID, time: LocalDateTime) {
        val updatedUsersCount = userRepository.updateLastVisitTime(userId, time)
        if (updatedUsersCount == 0) throw UserNotFoundException("User with id: $userId not found")
    }

    @Transactional
    fun updateUserStatus(userId: UUID, status: User.Status) {
        val updatedUserCount = userRepository.updateStatus(userId, status)
        if (updatedUserCount == 0) throw UserNotFoundException("User with id: $userId not found")
        eventPublisher.publish(createUserUpdatedEvent(status))
        /*
        todo: if the user is blocked call logout function to invalidate his access token
         */
    }
}