package net.thechance.identity.service

import identity.service.exception.InvalidRefreshTokenException
import net.thechance.identity.api.dto.AuthResponse
import net.thechance.identity.entity.LoginLog
import net.thechance.identity.entity.User
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.exception.UserIsBlockedException
import net.thechance.identity.security.JwtService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class AuthenticationService(
    private val userService: UserService,
    private val refreshRepo: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val loginLogService: LoginLogService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(phoneNumber: String, password: String, ipAddress: String): AuthResponse {
        if (isUserBlocked(ipAddress)) {
            throw UserIsBlockedException("User with phone Number: $phoneNumber is blocked")
        }
        val user = userService.findByPhoneNumber(phoneNumber)
        val isPasswordCorrect = passwordEncoder.matches(password, user.password)
        addUserToLogs(user = user, isSuccess = isPasswordCorrect, ipAddress = ipAddress)
        if (!isPasswordCorrect) throw BadCredentialsException("Invalid Credentials")
        return generateAuthResponse(user)
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val token = refreshTokenService.validateRefreshToken(refreshToken) ?: throw InvalidRefreshTokenException()
        refreshRepo.delete(token)
        return generateAuthResponse(token.user)
    }

    private fun addUserToLogs(
        user: User,
        isSuccess: Boolean,
        ipAddress: String
    ) {
        val loginLog = LoginLog(user = user, isSuccess = isSuccess, ipAddress = ipAddress)
        loginLogService.addLoginLog(loginLog)
    }

    private fun isUserBlocked(ipAddress: String): Boolean {
        val loginLogs = loginLogService.getLoginLogsByIpAddress(ipAddress, 5)
            .filter { !it.isSuccess }
            .ifEmpty { return false }

        if (isUserLoginRetriesWithInLimit(loginLogs)) return false
        if (isCurrentTimeWithInBlockRange(loginLogs)) return false
        return isDurationBetweenFirstAndLastLoginTimeWithInBlockRange(loginLogs)
    }

    private fun isUserLoginRetriesWithInLimit(loginLogs: List<LoginLog>) = loginLogs.size < MAX_LOGIN_ATTEMPTS

    private fun isCurrentTimeWithInBlockRange(loginLogs: List<LoginLog>): Boolean {
        val lastTimeToLogin = loginLogs.first().loginTime
        val now = Instant.now()
        val durationSinceLastLogin = Duration.between(lastTimeToLogin, now)
        return durationSinceLastLogin.toMinutes() >= MAX_BLOCK_TIME_IN_MINUTES
    }

    private fun isDurationBetweenFirstAndLastLoginTimeWithInBlockRange(loginLogs: List<LoginLog>): Boolean {
        val lastTimeToLogin = loginLogs.first().loginTime
        val firstTimeToLogin = loginLogs.last().loginTime
        val durationBetweenFirstAndLastLogin = Duration.between(firstTimeToLogin, lastTimeToLogin)
        return durationBetweenFirstAndLastLogin.toMinutes() <= BLOCK_TIME_IN_MINUTES
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        val accessToken = jwtService.generateToken(user)
        val refreshToken = refreshTokenService.createRefreshToken(user).refreshToken
        return AuthResponse(accessToken, refreshToken)
    }

    private companion object {
        const val MAX_LOGIN_ATTEMPTS = 5
        const val MAX_BLOCK_TIME_IN_MINUTES = 15L
        const val BLOCK_TIME_IN_MINUTES = 2L
    }
}