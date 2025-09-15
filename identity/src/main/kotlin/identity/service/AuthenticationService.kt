package identity.service

import identity.config.exceptionHandling.InvalidRefreshTokenException
import identity.dto.AuthResponse
import identity.entity.User
import identity.repository.RefreshTokenRepository
import identity.security.JwtService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    val userService: UserService,
    private val refreshRepo: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder,
) {

    fun login(phoneNumber: String, password: String): AuthResponse {
        val user = userService.findByPhoneNumber(phoneNumber)

        if (!passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("Invalid Credentials")
        }
        return generateAuthResponse(user)
    }

    fun refresh(refreshToken: String): AuthResponse {
        val token = refreshTokenService.validateRefreshToken(refreshToken) ?: throw InvalidRefreshTokenException()
        refreshRepo.delete(token)
        return generateAuthResponse(token.user)
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        val accessToken = jwtService.generateToken(user)
        val refreshToken = refreshTokenService.createRefreshToken(user).refreshToken
        return AuthResponse(accessToken, refreshToken)
    }
}