package net.thechance.identity.service

import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class UpdateUserProfileImageService(
    private val identityImageStorageService: IdentityImageStorageService,
    private val userRepository: UserRepository
) {

    operator fun invoke(userId: UUID, file: MultipartFile) {
        val imageUrl = identityImageStorageService.uploadImage(
            file = file,
            fileName = "$userId",
            folderName = FOLDER_NAME
        )

        userRepository.findById(userId)
            .orElseThrow { throw UserNotFoundException("User with id: $userId not found") }
            .copy(imageUrl = imageUrl)
            .also { userRepository.save(it) }
    }

    private companion object {
        const val FOLDER_NAME = "profile"
    }
}