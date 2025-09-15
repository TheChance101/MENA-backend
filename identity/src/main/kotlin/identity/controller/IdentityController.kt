package identity.controller

import identity.dto.RefreshRequest
import jakarta.validation.Valid
import identity.dto.AuthRequest
import identity.dto.AuthResponse
import identity.service.AuthenticationService
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

    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authenticationService.refresh(request.refreshToken))
    }
}