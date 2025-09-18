package net.thechance.identity.api.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import net.thechance.identity.api.dto.AuthRequest
import net.thechance.identity.api.dto.AuthResponse
import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.AuthenticationException
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.InvalidIpException
import net.thechance.identity.exception.UserIsBlockedException
import net.thechance.identity.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/identity")
class IdentityController(
    private val authenticationService: AuthenticationService
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

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<ErrorResponse?> {
        return when (exception) {
            is UserIsBlockedException -> {
                ResponseEntity.status(403).body(ErrorResponse("User is blocked"))
            }
            is InvalidCredentialsException -> {
                ResponseEntity.status(401).body(ErrorResponse("Invalid credentials"))
            }
            is InvalidIpException -> {
                ResponseEntity.badRequest().body(ErrorResponse("Invalid IP"))
            }

            else -> {
                ResponseEntity.internalServerError().body(ErrorResponse("unknown error happened"))
            }
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse?> {
        return ResponseEntity.internalServerError().body(ErrorResponse("unknown error happened"))
    }
}