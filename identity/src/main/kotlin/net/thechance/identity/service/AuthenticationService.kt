package net.thechance.identity.service

import net.thechance.identity.api.dto.auth.AuthResponse
import net.thechance.identity.entity.User
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.InvalidRefreshTokenException
import net.thechance.identity.exception.UserIsBlockedException
import net.thechance.identity.repository.RefreshTokenRepository
import net.thechance.identity.security.JwtService
import net.thechance.identity.service.model.Country
import net.thechance.identity.service.model.LocalizedCountry
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AuthenticationService(
    private val userService: UserService,
    private val refreshRepo: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val messageSource: MessageSource
) {

    fun login(phoneNumber: String, password: String): AuthResponse {
        val user = userService.findByPhoneNumber(phoneNumber)
        if (user.status == User.Status.BLOCKED) {
            throw UserIsBlockedException("User with phone Number: $phoneNumber is blocked")
        }
        val isPasswordCorrect = passwordEncoder.matches(password, user.password)
        if (isPasswordCorrect) {
            userService.updateUserLastLoginTime(userId = user.id, time = LocalDateTime.now())
        } else {
            throw InvalidCredentialsException("Invalid Credentials")
        }
        return generateAuthResponse(user)
    }

    fun logout(userId: UUID) {
        refreshTokenService.deleteUserRefreshTokens(userId)
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

    fun getCountries(): List<LocalizedCountry> {
        val currentLocale = LocaleContextHolder.getLocale()
        return Country.entries.map {
            it.getLocalizedCountryName(messageSource, currentLocale)
        }
    }
}