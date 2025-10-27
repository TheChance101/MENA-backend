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
import java.util.*

@RestController
@RequestMapping("/identity/authentication")
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
        val authResponse = authenticationService.login(
            phoneNumber = request.phoneNumber,
            password = request.password,
            ipAddress = ipAddress
        )
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequest
    ): ResponseEntity<AuthResponse> {
        val response = authenticationService.refreshToken(request.refreshToken)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/request-reset-password-otp")
    fun requestResetPasswordOtp(
        @RequestBody @Valid request: RequestOtpRequest,
    ): ResponseEntity<RequestOtpResponse> {
        val response = resetPasswordService.requestOtp(
            phoneNumber = request.phoneNumber,
            defaultRegion = request.defaultRegion
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify-reset-password-otp")
    fun verifyOtp(
        @RequestBody @Valid request: VerifyOtpRequest,
    ): ResponseEntity<Unit> {
        val response = resetPasswordService.verifyOtp(
            otp = request.otp,
            sessionId = UUID.fromString(request.sessionId)
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody @Valid request: ResetPasswordRequest
    ): ResponseEntity<Unit> {
        val response = resetPasswordService.resetPassword(
            newPassword = request.newPassword,
            confirmPassword = request.confirmPassword,
            sessionId = UUID.fromString(request.sessionId)
        )
        return ResponseEntity.ok(response)
    }
}