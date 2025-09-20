package net.thechance.identity.api.controller

import jakarta.servlet.http.HttpServletRequest
import identity.api.dto.RefreshTokenRequest
import jakarta.validation.Valid
import net.thechance.identity.api.dto.AuthRequest
import net.thechance.identity.api.dto.AuthResponse
import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.*
import net.thechance.identity.service.AuthenticationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/identity")
class IdentityController(
    private val authenticationService: AuthenticationService,
    private val logger: Logger = LoggerFactory.getLogger(IdentityController::class.java)
) {
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: AuthRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<AuthResponse> {
        val ipAddress = httpRequest.remoteAddr ?: throw InvalidIpException("Invalid IP")
        val authResponse = authenticationService.login(request.phoneNumber, request.password, ipAddress)
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authenticationService.refreshToken(request.refreshToken))
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<ErrorResponse?> {
        logger.error("Authentication failed: ${exception.message}", exception)
        return when (exception) {
            is UserIsBlockedException -> {
                ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse("User is blocked"))
            }

            is InvalidCredentialsException -> {
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse("Invalid credentials"))
            }

            is InvalidIpException -> {
                ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse("Invalid IP"))
            }

            is InvalidRefreshTokenException -> {
                ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse("Invalid refresh token"))
            }

            else -> {
                ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse("unknown error happened"))
            }
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse?> {
        logger.error("Unknown error happened: ${exception.message}", exception)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("unknown error happened"))
    }
}