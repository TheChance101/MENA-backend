package net.thechance.identity.service

import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.PasswordNotUpdatedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ResetPasswordService(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {
    fun resetPassword(phoneNumber: String, newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) throw PasswordMismatchException()
        val encodedPassword = passwordEncoder.encode(newPassword)
        val isPasswordUpdated = userService.updatePasswordByPhoneNumber(phoneNumber, encodedPassword)
        if (!isPasswordUpdated) throw PasswordNotUpdatedException()
    }
}