package net.thechance.identity.service

import net.thechance.identity.api.dto.AuthResponse
import net.thechance.identity.entity.AdminUser
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.InvalidRefreshTokenException
import net.thechance.identity.repository.AdminRefreshTokenRepository
import net.thechance.identity.repository.AdminUserRepository
import net.thechance.identity.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminAuthenticationService(
    private val adminUserRepository: AdminUserRepository,
    private val adminRefreshTokenRepository: AdminRefreshTokenRepository,
    private val jwtService: JwtService,
    private val adminRefreshTokenService: AdminRefreshTokenService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(username: String, password: String): AuthResponse {
        val admin = adminUserRepository.findByUsername(username)
            ?: throw InvalidCredentialsException("Invalid Credentials")

        val isPasswordCorrect = passwordEncoder.matches(password, admin.password)
        if (!isPasswordCorrect) throw InvalidCredentialsException("Invalid Credentials")

        return generateAuthResponse(admin)
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val token = adminRefreshTokenService.validateRefreshToken(refreshToken) ?: throw InvalidRefreshTokenException()
        adminRefreshTokenRepository.delete(token)
        return generateAuthResponse(token.adminUser)
    }

    @Transactional
    fun logout(adminId: UUID) = adminRefreshTokenRepository.removeByAdminUserId(adminId)

    private fun generateAuthResponse(adminUser: AdminUser): AuthResponse {
        val accessToken = jwtService.generateToken(adminUser)
        val refreshToken = adminRefreshTokenService.createRefreshToken(adminUser).refreshToken
        return AuthResponse(accessToken, refreshToken)
    }
}
