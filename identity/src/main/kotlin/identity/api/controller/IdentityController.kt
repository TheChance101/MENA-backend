package net.thechance.identity.controller

import identity.api.dto.RefreshTokenRequest
import jakarta.validation.Valid
import net.thechance.identity.dto.AuthRequest
import net.thechance.identity.dto.AuthResponse
import net.thechance.identity.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/identity")
class IdentityController(
    val authenticationService: AuthenticationService
) {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid phone number or password")
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AuthRequest): ResponseEntity<AuthResponse> {
        val authResponse = authenticationService.login(request.phoneNumber, request.password)
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authenticationService.refreshToken(request.refreshToken))
    }
}