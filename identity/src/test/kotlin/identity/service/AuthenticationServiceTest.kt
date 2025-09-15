package identity.service

import com.google.common.truth.Truth
import identity.dto.AuthResponse
import identity.entity.RefreshToken
import identity.entity.User
import identity.repository.RefreshTokenRepository
import identity.security.JwtService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthenticationServiceTest {
    val userService: UserService = mockk(relaxed = true)
    val refreshRepo: RefreshTokenRepository = mockk(relaxed = true)
    val jwtService: JwtService = mockk(relaxed = true)
    val refreshTokenService: RefreshTokenService = mockk(relaxed = true)
    val passwordEncoder: PasswordEncoder = mockk(relaxed = true)

    lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(
            userService = userService,
            refreshRepo = refreshRepo,
            jwtService = jwtService,
            refreshTokenService = refreshTokenService,
            passwordEncoder = passwordEncoder,
        )
    }

    @Test
    fun `login() should return tokens when login credentials are valid`() {
        val phoneNumber = "1234"
        val password = "encodedPass"
        val user = getUser(phoneNumber = phoneNumber, password = password)
        val accessToken = "access-token"
        val refreshToken = RefreshToken(1L, "refresh-token", 0L, user)

        every { userService.findByPhoneNumber(phoneNumber) } returns user
        every { passwordEncoder.matches(phoneNumber, password) } returns true
        every { jwtService.generateToken(user) } returns accessToken
        every { refreshTokenService.createRefreshToken(user) } returns refreshToken

        val response = authenticationService.login(phoneNumber, phoneNumber)
        val expected = AuthResponse(accessToken, refreshToken.refreshToken)
        Truth.assertThat(response).isEqualTo(expected)
    }

    @Test
    fun `login() should throw BadCredentialsException when password does not match`() {
        val password = "wrong password"
        val user = getUser(password = password)

        every { userService.findByPhoneNumber(user.phoneNumber) } returns (user)
        every { passwordEncoder.matches(password, "correct password") } returns false

        assertThrows<BadCredentialsException> {
            authenticationService.login(user.phoneNumber, password)
        }
    }

    @Test
    fun `refresh() should return new auth response when refresh token is valid`() {
        val user = getUser()

        val oldRefreshToken = "old-refresh"
        val newRefreshToken = "new-refresh"
        val oldToken = RefreshToken(1L, oldRefreshToken, 0L, user)
        val newToken = RefreshToken(1L, newRefreshToken, 0L, user)

        val accessToken = "access-token"

        every { refreshTokenService.validateRefreshToken(oldRefreshToken) } returns (oldToken)
        every { jwtService.generateToken(user) } returns (accessToken)
        every { refreshTokenService.createRefreshToken(user) } returns (newToken)

        val response = authenticationService.refresh(oldRefreshToken)
        val expected = AuthResponse(accessToken, newRefreshToken)
        Truth.assertThat(response).isEqualTo(expected)
    }

    @Test
    fun `refresh() should delete old refresh tokens when refresh token is valid`() {
        val user = getUser()

        val oldRefreshToken = "old-refresh"
        val newRefreshToken = "new-refresh"
        val oldToken = RefreshToken(1L, oldRefreshToken, 0L, user)
        val newToken = RefreshToken(1L, newRefreshToken, 0L, user)

        val accessToken = "access-token"

        every { refreshTokenService.validateRefreshToken(oldRefreshToken) } returns (oldToken)
        every { jwtService.generateToken(user) } returns (accessToken)
        every { refreshTokenService.createRefreshToken(user) } returns (newToken)

        authenticationService.refresh(oldRefreshToken)
        verify { refreshRepo.delete(oldToken) }
    }

    fun getUser(
        phoneNumber: String = "1234",
        password: String = "encodedPass"
    ): User {
        return User(
            id = UUID.randomUUID(),
            phoneNumber = phoneNumber,
            password = password
        )
    }
}