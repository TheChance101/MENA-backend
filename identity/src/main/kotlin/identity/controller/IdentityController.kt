package net.thechance.identity.controller

import jakarta.validation.Valid
import net.thechance.identity.dto.AuthRequest
import net.thechance.identity.dto.AuthResponse
import net.thechance.identity.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}