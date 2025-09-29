package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import net.thechance.identity.utils.DummyIpAddresses
import net.thechance.identity.utils.DummyUserLogs
import net.thechance.identity.utils.DummyUsers
import io.mockk.every
import io.mockk.mockk
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.UserIsBlockedException
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.security.JwtService
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.security.crypto.password.PasswordEncoder

class AuthenticationServiceTest {
    private val userService: UserService = mockk(relaxed = true)
    private val refreshTokenRepository: RefreshTokenRepository = mockk(relaxed = true)
    private val refreshTokenService: RefreshTokenService = mockk(relaxed = true)
    private val loginLogService: LoginLogService = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val jwtService: JwtService = mockk(relaxed = true)
    private val authenticationService = AuthenticationService(
        userService = userService,
        jwtService = jwtService,
        refreshTokenService = refreshTokenService,
        loginLogService = loginLogService,
        passwordEncoder = passwordEncoder,
        refreshRepo = refreshTokenRepository
    )

    @Test
    fun `should throw UserIsBlockedException when user is trying to login 5 times with exist phone number and wrong password`() {
        val blockedUserLogs = DummyUserLogs.loginLogsForBlockedUser
        val user = blockedUserLogs.first().user
        val ipAddress = blockedUserLogs.first().ipAddress
        every { loginLogService.getLoginLogsByIpAddress(ipAddress, 5) } returns blockedUserLogs
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(UserIsBlockedException::class.java) {
            authenticationService.login(user.phoneNumber, user.password, ipAddress)
        }
    }

    @Test
    fun `should throw InvalidCredentialsException when user is trying to login with exist phone number and wrong password after block released`() {
        val userLogs = DummyUserLogs.loginLogsForUserAfterBlockReleased
        val user = userLogs.first().user
        val ipAddress = userLogs.first().ipAddress
        every { loginLogService.getLoginLogsByIpAddress(ipAddress, 5) } returns userLogs
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(InvalidCredentialsException::class.java) {
            authenticationService.login(user.phoneNumber, user.password, ipAddress)
        }
    }

    @Test
    fun `should return response when user is trying to login with exist phone number and correct password`() {
        val user = DummyUsers.validUser1
        val ipAddress = DummyIpAddresses.validIpAddress1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        val response = authenticationService.login(user.phoneNumber, user.password, ipAddress)

        assertThat(response).isNotNull()
    }

    @Test
    fun `should throw InvalidCredentialsException when user is trying to login with exist phone number and wrong password`() {
        val user = DummyUsers.userWithInvalidPassword
        val ipAddress = DummyIpAddresses.validIpAddress1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(InvalidCredentialsException::class.java) {
            authenticationService.login(user.phoneNumber, user.password, ipAddress)
        }
    }

    @Test
    fun `should throw InvalidCredentialsException when user is trying to login with phone number not exist`() {
        val user = DummyUsers.userWithInvalidPhoneNumber
        val ipAddress = DummyIpAddresses.validIpAddress1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(InvalidCredentialsException::class.java) {
            authenticationService.login(user.phoneNumber, user.password, ipAddress)
        }
    }
}