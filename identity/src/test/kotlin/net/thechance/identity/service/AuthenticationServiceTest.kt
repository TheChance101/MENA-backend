package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.entity.RefreshToken
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.security.JwtService
import net.thechance.identity.utils.DummyUsers
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.security.crypto.password.PasswordEncoder

class AuthenticationServiceTest {
    private val userService: UserService = mockk(relaxed = true)
    private val refreshTokenRepository: RefreshTokenRepository = mockk(relaxed = true)
    private val refreshTokenService: RefreshTokenService = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val jwtService: JwtService = mockk(relaxed = true)
    private val authenticationService = AuthenticationService(
        userService = userService,
        jwtService = jwtService,
        refreshTokenService = refreshTokenService,
        passwordEncoder = passwordEncoder,
        refreshRepo = refreshTokenRepository
    )

    @Test
    fun `should return response when user is trying to login with exist phone number and correct password`() {
        val user = DummyUsers.validUser1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        val response = authenticationService.login(user.phoneNumber, user.password)

        assertThat(response).isNotNull()
    }

    @Test
    fun `should update last login time when user is trying to login with exist phone number and correct password`() {
        val user = DummyUsers.validUser1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        authenticationService.login(user.phoneNumber, user.password)

        verify(exactly = 1) { userService.updateUserLastLoginTime(user.id, any()) }
    }

    @Test
    fun `should update last visit time when user is trying to login with exist phone number and correct password`() {
        val user = DummyUsers.validUser1
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        authenticationService.login(user.phoneNumber, user.password)

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
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(InvalidCredentialsException::class.java) {
            authenticationService.login(user.phoneNumber, user.password)
        }
    }

    @Test
    fun `should throw InvalidCredentialsException when user is trying to login with phone number not exist`() {
        val user = DummyUsers.userWithInvalidPhoneNumber
        every { userService.findByPhoneNumber(user.phoneNumber) } returns user

        assertThrows(InvalidCredentialsException::class.java) {
            authenticationService.login(user.phoneNumber, user.password)
        }
    }
}