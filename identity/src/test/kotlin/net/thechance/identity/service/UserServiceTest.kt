package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.entity.User
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.utils.createUser
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class UserServiceTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val identityImageStorageService: IdentityImageStorageService = mockk(relaxed = true)
    private val userService = UserService(userRepository, identityImageStorageService, "profile-images")

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
    fun `findUsersByQuery() should return page of users matching the query when they exist `() {
        val usersPage = PageImpl(List(3) { createUser() })
        every { userRepository.findByFullNameOrPhoneNumber(any(), any()) } returns usersPage

        val result = userService.findUsersByQuery(query, pageable)

        assertThat(result.content).containsExactlyElementsIn(usersPage.content)
    }

    @Test
    fun `findUsersByQuery() should propagate user repository exceptions`() {
        val testException = RuntimeException("Test exception")
        every { userRepository.findByFullNameOrPhoneNumber(any(), any()) } throws testException

        assertThrows(testException::class.java) {
            userService.findUsersByQuery(query, pageable)
        }
    }

    @Test
    fun `updateUserLastLoginTime() should complete successfully when user exists`() {
        val now = LocalDateTime.now()
        every { userRepository.updateLastLoginTime(id, now) } returns 1

        userService.updateUserLastLoginTime(id, now)

        verify(exactly = 1) { userRepository.updateLastLoginTime(id, now) }
    }

    @Test
    fun `updateUserLastLoginTime() should throw UserNotFoundException when user is not found`() {
        val now = LocalDateTime.now()
        every{ userRepository.updateLastLoginTime(id, LocalDateTime.now()) } returns 0

        assertThrows(UserNotFoundException::class.java) {
            userService.updateUserLastLoginTime(id, now)
        }
    }

    @Test
    fun `updateUserLastVisitTime() should complete successfully when user exists`() {
        val now = LocalDateTime.now()
        every{ userRepository.updateLastVisitTime(id, now) } returns 1

        userService.updateUserLastVisitTime(id, now)

        verify(exactly = 1) { userRepository.updateLastVisitTime(id, now) }
    }

    @Test
    fun `updateUserLastVisitTime() should throw UserNotFoundException when user is not found`() {
        val now = LocalDateTime.now()
        every{ userRepository.updateLastVisitTime(id, LocalDateTime.now()) } returns 0

        assertThrows(UserNotFoundException::class.java) {
            userService.updateUserLastVisitTime(id, now)
        }
    }

    @Test
    fun `updateUserStatus() should complete successfully when user exists`() {
        val newStatus = User.Status.ACTIVE
        every{ userRepository.updateStatus(id, newStatus) } returns 1

        userService.updateUserStatus(id, newStatus)

        verify(exactly = 1) { userRepository.updateStatus(id, newStatus) }
    }

    @Test
    fun `updateUserStatus() should throw UserNotFoundException when user is not found`() {
        val newStatus = User.Status.ACTIVE
        every{ userRepository.updateStatus(id, newStatus) } returns 0

        assertThrows(UserNotFoundException::class.java) {
            userService.updateUserStatus(id, newStatus)
        }
    }

    companion object {
        private val user = createUser()
        private val phoneNumber = user.phoneNumber
        private val id = user.id
        private const val PASSWORD = "00000000"
        private val updatedUser = user.copy(password = PASSWORD)
        private val pageable = PageRequest.of(0, 10)
        private val query = "john"
    }
}