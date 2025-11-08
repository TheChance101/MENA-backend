package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.entity.RefreshToken
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.UserIpIsBlockedException
import net.thechance.identity.exception.UserIsBlockedException
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.security.JwtService
import net.thechance.identity.utils.DummyIpAddresses
import net.thechance.identity.utils.DummyUserLogs
import net.thechance.identity.utils.DummyUsers
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

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
    fun `should throw UserIpIsBlockedException when user is trying to login 5 times with exist phone number and wrong password`() {
        val blockedUserLogs = DummyUserLogs.loginLogsForBlockedUser
        val user = blockedUserLogs.first().user
        val ipAddress = blockedUserLogs.first().ipAddress
        every { loginLogService.getLoginLogsByIpAddress(ipAddress, 5) } returns blockedUserLogs
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(UserIpIsBlockedException::class.java) {
            authenticationService.login(user.phoneNumber, user.password, ipAddress)
        }
    }

    @Test
    fun `should throw UserIsBlockedException when user status is blocked`() {
        val user = DummyUsers.blockedUser
        val ipAddress = DummyIpAddresses.validIpAddress1
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
    fun `should update last login time when user is trying to login with exist phone number and correct password`() {
        val user = DummyUsers.validUser1
        val ipAddress = DummyIpAddresses.validIpAddress1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        authenticationService.login(user.phoneNumber, user.password, ipAddress)

        verify(exactly = 1) { userService.updateUserLastLoginTime(user.id, any()) }
    }

    @Test
    fun `should update last visit time when user is trying to login with exist phone number and correct password`() {
        val user = DummyUsers.validUser1
        val ipAddress = DummyIpAddresses.validIpAddress1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        authenticationService.login(user.phoneNumber, user.password, ipAddress)

        verify(exactly = 1) { userService.updateUserLastVisitTime(user.id, any()) }
    }

    @Test
    fun `should update last visit time when user refreshes access token`() {
        val refreshToken = ""
        val user = DummyUsers.validUser1
        val resultToken = RefreshToken(refreshToken = refreshToken, expiresIn = 1000L, user = user)
        every { refreshTokenService.validateRefreshToken(refreshToken) } returns resultToken

        authenticationService.refreshToken(refreshToken)

        verify(exactly = 1) { userService.updateUserLastVisitTime(user.id, any()) }
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

    @Test
    fun `should delete user refresh tokens successfully when user logs out`() {
        val userId = UUID.randomUUID()

        authenticationService.logout(userId)

        verify(exactly = 1) { refreshTokenService.deleteUserRefreshTokens(userId) }
    }

    @Test
    fun `should throw exception when deleting user refresh tokens fails during logout`() {
        val userId = UUID.randomUUID()

        every { refreshTokenService.deleteUserRefreshTokens(userId) } throws RuntimeException("Database error")

        assertThrows(RuntimeException::class.java) {
            authenticationService.logout(userId)
        }
    }
}