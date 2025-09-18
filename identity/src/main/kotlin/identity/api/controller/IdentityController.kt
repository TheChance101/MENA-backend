package net.thechance.identity.api.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import net.thechance.identity.api.dto.AuthRequest
import net.thechance.identity.api.dto.AuthResponse
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
        val ipAddress = httpRequest.remoteAddr ?: throw IllegalStateException("Invalid IP")
        val authResponse = authenticationService.login(request.phoneNumber, request.password, ipAddress)
        return ResponseEntity.ok(authResponse)
    }
}