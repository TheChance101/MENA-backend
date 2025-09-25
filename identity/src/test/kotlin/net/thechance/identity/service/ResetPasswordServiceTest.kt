package net.thechance.identity.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.PasswordNotUpdatedException
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.security.crypto.password.PasswordEncoder

class ResetPasswordServiceTest {
    private val userService: UserService = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val resetPasswordService: ResetPasswordService by lazy {
        ResetPasswordService(
            userService = userService,
            passwordEncoder = passwordEncoder
        )
    }

    @Test
    fun `resetPassword() should throw PasswordMismatchException when password and confirm password do not match`() {
        assertThrows(PasswordMismatchException::class.java) {
            resetPasswordService.resetPassword(phoneNumber, newPassword, wrongConfirmPassword)
        }
    }

    @Test
    fun `resetPassword() should update password and not throwing exceptions when updatePasswordByPhoneNumber returns true`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true

        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
    }

    @Test
    fun `resetPassword() should call updatePasswordByPhoneNumber one time when called with correct data`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true

        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)

        verify(exactly = 1) { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) }
    }

    @Test
    fun `resetPassword() should call encode one time when called with correct data`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true

        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)

        verify(exactly = 1) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `resetPassword() should throw InvalidCredentialsException when updatePasswordByPhoneNumber throws`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every {
            userService.updatePasswordByPhoneNumber(
                phoneNumber,
                newPassword
            )
        } throws InvalidCredentialsException("")

        assertThrows(InvalidCredentialsException::class.java) {
            resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
        }
    }

    @Test
    fun `resetPassword() should throw PasswordNotUpdatedException when updatePasswordByPhoneNumber returns false`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns false

        assertThrows(PasswordNotUpdatedException::class.java) {
            resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
        }
    }
}

private const val phoneNumber = "+201145236258"
private const val newPassword = "12345678"
private const val confirmPassword = "12345678"
private const val wrongConfirmPassword = "12345679"
