package net.thechance.identity.controller

import jakarta.validation.Valid
import net.thechance.identity.dto.AuthRequest
import net.thechance.identity.dto.AuthResponse
import net.thechance.identity.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/identity")
class IdentityController(
    val authenticationService: AuthenticationService
) {
    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AuthRequest): ResponseEntity<AuthResponse> {
        val authResponse = authenticationService.login(request.phoneNumber, request.password)
        return ResponseEntity.ok(authResponse)
    }
}