package net.thechance.identity.api.controller.adminAuth

import jakarta.validation.Valid
import net.thechance.identity.api.dto.auth.AdminAuthRequest
import net.thechance.identity.api.dto.auth.AuthResponse
import net.thechance.identity.api.dto.auth.RefreshTokenRequest
import net.thechance.identity.service.AdminAuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/identity/admin/authentication")
class AdminAuthController(
    private val adminAuthenticationService: AdminAuthenticationService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AdminAuthRequest): ResponseEntity<AuthResponse> {
        val response = adminAuthenticationService.login(request.username, request.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = adminAuthenticationService.refreshToken(request.refreshToken)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal adminId: UUID
    ): ResponseEntity<Unit> {
        adminAuthenticationService.logout(adminId)
        return ResponseEntity.ok().build()
    }
}