package net.thechance.identity.service

import io.mockk.every
import io.mockk.mockk
import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.UnauthorizedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.utils.createUser
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

class ChangePasswordServiceTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val changePasswordService = ChangePasswordService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder
    )

    @Test
    fun `changePassword() should throw PasswordMismatchException when new and confirm passwords not match`() {
        assertThrows(PasswordMismatchException::class.java) {
            changePasswordService.changePassword(
                userId = USER_ID,
                currentPassword = CURRENT_PASSWORD,
                newPassword = NEW_PASSWORD,
                confirmPassword = INVALID_CONFIRM_PASSWORD
            )
        }
    }

    @Test
    fun `changePassword() should throw UserNotFoundException when user not found with passed id`() {
        every { userRepository.findByIdOrNull(any()) } returns null

        assertThrows(UserNotFoundException::class.java) {
            changePasswordService.changePassword(
                userId = USER_ID,
                currentPassword = CURRENT_PASSWORD,
                newPassword = NEW_PASSWORD,
                confirmPassword = CONFIRM_PASSWORD
            )
        }
    }

    @Test
    fun `changePassword() should throw UnauthorizedException when current password not matches that in db`() {
        every { userRepository.findByIdOrNull(any()) } returns UNAUTHENTICATED_USER
        assertThrows(UnauthorizedException::class.java) {
            changePasswordService.changePassword(
                userId = USER_ID,
                currentPassword = CURRENT_PASSWORD,
                newPassword = NEW_PASSWORD,
                confirmPassword = CONFIRM_PASSWORD
            )
        }
    }

    @Test
    fun `changePassword() should pass when all data is valid`() {
        every { userRepository.findByIdOrNull(any()) } returns USER
        every { passwordEncoder.matches(any(), any()) } returns true
        every { userRepository.save(any()) } returns UPDATED_USER

        changePasswordService.changePassword(
            userId = USER_ID,
            currentPassword = CURRENT_PASSWORD,
            newPassword = NEW_PASSWORD,
            confirmPassword = CONFIRM_PASSWORD
        )
    }

    companion object {
        val USER_ID = UUID.fromString("11111111-2222-3333-4444-555555555555")!!
        const val CURRENT_PASSWORD = "Abcd1234"
        const val NEW_PASSWORD = "12345678"
        const val CONFIRM_PASSWORD = "12345678"
        const val CURRENT_ENCODED_PASSWORD = "$2a$10\$WKLg8W/dBOxlzwDRm8am8epx.dfFB4W0Rr1LHeSnVRN6ww1NeD8xi"
        const val NEW_ENCODED_PASSWORD = "$2a$10\$q57k1OPkevJbERxcvNqat.3f6R2/ubzVLdgwDRIC/H4xIhmEuUgbG"
        const val INVALID_CONFIRM_PASSWORD = "123456789"

        val UNAUTHENTICATED_USER = createUser(
            USER_ID,
            password = "QWEASDZXC"
        )

        val USER = createUser(
            USER_ID,
            password = CURRENT_ENCODED_PASSWORD
        )

        val UPDATED_USER = createUser(
            USER_ID,
            password = NEW_ENCODED_PASSWORD
        )
    }
}