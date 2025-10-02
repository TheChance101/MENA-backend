package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.utils.createUser
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.data.repository.findByIdOrNull

class UserServiceTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val userService = UserService(userRepository)

    @Test
    fun `findByPhoneNumber() should return User when user exists`() {
        every { userRepository.findByPhoneNumber(any()) } returns user

        val resultUser = userService.findByPhoneNumber(phoneNumber)

        assertThat(resultUser).isNotNull()
    }

    @Test
    fun `findByPhoneNumber() should throw InvalidCredentialsException when user not exists`() {
        every { userRepository.findByPhoneNumber(any()) } returns null

        assertThrows(InvalidCredentialsException::class.java) { userService.findByPhoneNumber(phoneNumber) }
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

        val isUserExists = userService.userExists(id)

        assertThat(isUserExists).isTrue()
    }

    @Test
    fun `userExists() should return false when the user not exists`() {
        every { userRepository.existsById(any()) } returns false

        val isUserExists = userService.userExists(id)

        assertThat(isUserExists).isFalse()
    }

    @Test
    fun `userExists() should call userExists in userService one time when called`() {
        every { userRepository.existsById(any()) } returns true

        userService.userExists(id)

        verify(exactly = 1) { userRepository.existsById(any()) }
    }

    @Test
    fun `updatePasswordByPhoneNumber() should return true when user updated`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns updatedUser

        val isPasswordUpdated = userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)

        assertThat(isPasswordUpdated).isTrue()
    }

    @Test
    fun `updatePasswordByPhoneNumber() should return false when user not updated`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns user

        val isPasswordUpdated = userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)

        assertThat(isPasswordUpdated).isFalse()
    }

    @Test
    fun `updatePasswordByPhoneNumber() should throw PasswordNotUpdatedException when save in repository returns null`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns null

        assertThrows(PasswordNotUpdatedException::class.java) {
            userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)
        }
    }

    @Test
    fun `updatePasswordByPhoneNumber() should throw InvalidCredentialsException user not found`() {
        every { userRepository.findByPhoneNumber(any()) } returns null

        assertThrows(InvalidCredentialsException::class.java) {
            userService.updatePasswordByPhoneNumber(phoneNumber, PASSWORD)
        }
    }

    companion object {
        private val user = createUser()
        private val phoneNumber = user.phoneNumber
        private val id = user.id
        private const val PASSWORD = "00000000"
        private val updatedUser = user.copy(password = PASSWORD)
    }
}