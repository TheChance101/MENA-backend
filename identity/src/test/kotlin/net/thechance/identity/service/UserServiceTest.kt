package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.model.UserServiceModel
import net.thechance.identity.utils.createUser
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.multipart.MultipartFile
import java.util.*

class UserServiceTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val identityImageStorageService: IdentityImageStorageService = mockk(relaxed = true)
    private val userService = UserService(userRepository, identityImageStorageService, "profile-images")
    private val mockImageFile: MultipartFile = mockk(relaxed = true)

    @Test
    fun `findByPhoneNumber() should return User when user exists`() {
        every { userRepository.findByPhoneNumber(any()) } returns user

        val resultUser = userService.findByPhoneNumber(phoneNumber)

        assertThat(resultUser).isNotNull()
    }

    @Test
    fun `findByPhoneNumber() should throw UserNotFoundException when user not exists`() {
        every { userRepository.findByPhoneNumber(any()) } returns null

        assertThrows(UserNotFoundException::class.java) { userService.findByPhoneNumber(phoneNumber) }
    }

    @Test
    fun `findByPhoneNumber() should call findByPhoneNumber in userRepository one time when called`() {
        every { userRepository.findByPhoneNumber(any()) } returns user

        userService.findByPhoneNumber(phoneNumber)

        verify(exactly = 1) { userRepository.findByPhoneNumber(any()) }
    }

    @Test
    fun `findById() should return User when user exists`() {
        every { userRepository.findByIdOrNull(user.id) } returns user

        val resultUser = userService.findById(user.id)

        assertThat(resultUser).isNotNull()
    }

    @Test
    fun `findById() should throw UserNotFoundException when user not exists`() {
        every { userRepository.findByIdOrNull(any()) } returns null

        assertThrows(UserNotFoundException::class.java) { userService.findById(user.id) }
    }

    @Test
    fun `findById() should call findByIdOrNull in userRepository one time when called`() {
        every { userRepository.findByIdOrNull(any()) } returns user

        userService.findById(user.id)

        verify(exactly = 1) { userRepository.findByIdOrNull(any()) }
    }

    @Test
    fun `userExists() should return true when the user exists`() {
        every { userRepository.existsById(any()) } returns true

        val isUserExists = userService.userExists(userId)

        assertThat(isUserExists).isTrue()
    }

    @Test
    fun `userExists() should return false when the user not exists`() {
        every { userRepository.existsById(any()) } returns false

        val isUserExists = userService.userExists(userId)

        assertThat(isUserExists).isFalse()
    }

    @Test
    fun `userExists() should call userExists in userService one time when called`() {
        every { userRepository.existsById(any()) } returns true

        userService.userExists(userId)

        verify(exactly = 1) { userRepository.existsById(any()) }
    }

    @Test
    fun `updatePasswordByPhoneNumber() should return runs with no exceptions when password updated`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns updatedUser

        userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)
    }

    @Test
    fun `updatePasswordByPhoneNumber() should throw PasswordNotUpdatedException when password not updated`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns user

        assertThrows(PasswordNotUpdatedException::class.java) {
            userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)
        }

    }

    @Test
    fun `updatePasswordByPhoneNumber() should throw UserNotFoundException user not found`() {
        every { userRepository.findByPhoneNumber(any()) } returns null

        assertThrows(UserNotFoundException::class.java) {
            userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)
        }
    }

    @Test
    fun `updateUserProfile should update fields and save user when called`() {
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { userRepository.save(any()) } returns user

        val updatedUser = userService.updateUserProfile(userModel)

        assertThat(updatedUser.id).isEqualTo(userId)
        assertThat(updatedUser.username).isEqualTo(userModel.username)
        assertThat(updatedUser.firstName).isEqualTo(userModel.firstName)
    }

    @Test
    fun `updateUserImage should upload image, update user url, and save when called`() {
        every { userRepository.findById(any()) } returns Optional.of(user)
        every {
            identityImageStorageService.uploadImage(
                file = mockImageFile,
                fileName = any()
            )
        } returns NEW_IMAGE_URL
        every { userRepository.save(any()) } returns user.copy(imageUrl = NEW_IMAGE_URL)

        val resultUrl = userService.updateUserImage(userId, mockImageFile)

        assertThat(resultUrl).isEqualTo(NEW_IMAGE_URL)
    }

    @Test
    fun `deleteUserImage should delete from storage and set url to null when image exists`() {
        every { userRepository.findById(any()) } returns Optional.of(userWithImage)
        every { identityImageStorageService.deleteImage(any()) } just runs
        every { userRepository.save(any()) } returns userWithImageAsNull

        userService.deleteUserImage(userId)

        verify(exactly = 1) { identityImageStorageService.deleteImage(NEW_IMAGE_URL) }
        verify(exactly = 1) { userRepository.save(userWithImageAsNull) }
    }

    @Test
    fun `deleteUserImage should do nothing when user has no image url`() {
        every { userRepository.findById(any()) } returns Optional.of(userWithImageAsNull)

        userService.deleteUserImage(userId)

        verify(exactly = 0) { identityImageStorageService.deleteImage(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `userExistsByUserName should return true when user exists`() {
        every { userRepository.existsByUsername(any()) } returns true

        val result = userService.userExistsByUserName(user.username)

        assertThat(result).isTrue()
    }

    @Test
    fun `userExistsByUserName should return false when user does not exist`() {
        every { userRepository.existsByUsername(any()) } returns false

        val result = userService.userExistsByUserName(user.username)

        assertThat(result).isFalse()
    }

    @Test
    fun `userExistsByPhoneNumber should return true when user exists`() {
        every { userRepository.existsByPhoneNumber(any()) } returns true

        val result = userService.userExistsByPhoneNumber(user.phoneNumber)

        assertThat(result).isTrue()
    }

    @Test
    fun `userExistsByPhoneNumber should return false when user does not exist`() {
        every { userRepository.existsByPhoneNumber(any()) } returns false

        val result = userService.userExistsByPhoneNumber(user.phoneNumber)

        assertThat(result).isFalse()
    }

    @Test
    fun `saveUser should call repository save and return the result`() {
        every { userRepository.save(any()) } returns user

        val result = userService.saveUser(user)

        assertThat(result).isEqualTo(user)
    }

    companion object {
        private val user = createUser()
        private val phoneNumber = user.phoneNumber
        private val userId = user.id
        private const val PASSWORD = "00000000"
        private val updatedUser = user.copy(password = PASSWORD)
        private val userModel = UserServiceModel(
            id = userId,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            gender = user.gender
        )
        private const val NEW_IMAGE_URL = "http://example.com/new-image.jpg"
        private val userWithImage = user.copy(imageUrl = NEW_IMAGE_URL)
        private val userWithImageAsNull = user.copy(imageUrl = null)
    }
}