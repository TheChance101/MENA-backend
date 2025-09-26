package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.utils.DummyUsers
import org.junit.Assert.assertThrows
import org.junit.Test

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

        val isPasswordUpdated = userService.updatePasswordByPhoneNumber(phoneNumber, password)

        assertThat(isPasswordUpdated).isTrue()
    }

    @Test
    fun `updatePasswordByPhoneNumber() should return false when user not updated`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns user

        val isPasswordUpdated = userService.updatePasswordByPhoneNumber(phoneNumber, password)

        assertThat(isPasswordUpdated).isFalse()
    }

    @Test
    fun `updatePasswordByPhoneNumber() should throw PasswordNotUpdatedException when save in repository returns null`() {
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { userRepository.save(any()) } returns null

        assertThrows(PasswordNotUpdatedException::class.java) {
            userService.updatePasswordByPhoneNumber(phoneNumber, password)
        }
    }

    @Test
    fun `updatePasswordByPhoneNumber() should throw InvalidCredentialsException user not found`() {
        every { userRepository.findByPhoneNumber(any()) } returns null

        assertThrows(InvalidCredentialsException::class.java) {
            userService.updatePasswordByPhoneNumber(phoneNumber, password)
        }
    }
}

private val user = DummyUsers.validUser1
private val phoneNumber = user.phoneNumber
private val id = user.id
private val password = DummyUsers.validUser2.password
private val updatedUser = user.copy(password = password)