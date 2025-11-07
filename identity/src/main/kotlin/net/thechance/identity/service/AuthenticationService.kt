package net.thechance.identity.service

import net.thechance.identity.api.dto.auth.AuthResponse
import net.thechance.identity.entity.User
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.InvalidRefreshTokenException
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthenticationService(
    private val userService: UserService,
    private val refreshRepo: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(phoneNumber: String, password: String): AuthResponse {
        val user = userService.findByPhoneNumber(phoneNumber)
        val isPasswordCorrect = passwordEncoder.matches(password, user.password)
        if (isPasswordCorrect) userService.updateUserLastLoginTime(userId = user.id, time = LocalDateTime.now())
        else throw InvalidCredentialsException("Invalid Credentials")
        return generateAuthResponse(user)
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val token = refreshTokenService.validateRefreshToken(refreshToken) ?: throw InvalidRefreshTokenException()
        refreshRepo.delete(token)
        return generateAuthResponse(token.user)
    }
    private fun generateAuthResponse(user: User): AuthResponse {
        val accessToken = jwtService.generateToken(user)
        val refreshToken = refreshTokenService.createRefreshToken(user).refreshToken
        userService.updateUserLastVisitTime(userId = user.id, time = LocalDateTime.now())
        return AuthResponse(accessToken, refreshToken)
    }
}