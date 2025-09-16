package identity.service

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import net.thechance.identity.dto.AuthResponse
import net.thechance.identity.entity.RefreshToken
import net.thechance.identity.entity.User
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.security.JwtService
import net.thechance.identity.service.AuthenticationService
import net.thechance.identity.service.RefreshTokenService
import net.thechance.identity.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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