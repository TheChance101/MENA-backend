package net.thechance.identity.api.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import net.thechance.identity.api.dto.*
import net.thechance.identity.exception.InvalidIpException
import net.thechance.identity.service.AuthenticationService
import net.thechance.identity.service.ResetPasswordService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/identity")
class IdentityController(
    private val authenticationService: AuthenticationService,
    private val resetPasswordService: ResetPasswordService,
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

    @PostMapping("/request-reset-password-otp")
    fun requestResetPasswordOtp(
        @RequestBody @Valid request: RequestOtpRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<RequestOtpResponse> {
        val response = resetPasswordService.requestOtp(request.phoneNumber, request.defaultRegion)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify-otp")
    fun verifyOtp(
        @RequestBody @Valid request: VerifyOtpRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<VerifyOtpResponse> {
        val response = resetPasswordService.verifyOtp(request.otp, request.sessionId)
        return ResponseEntity.ok(response)
    }
}