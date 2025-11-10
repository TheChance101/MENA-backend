package net.thechance.identity.api.controller.identity

import jakarta.validation.Valid
import net.thechance.identity.api.dto.auth.AuthRequest
import net.thechance.identity.api.dto.auth.AuthResponse
import net.thechance.identity.api.dto.auth.CountryResponse
import net.thechance.identity.api.dto.auth.RefreshTokenRequest
import net.thechance.identity.api.dto.otp.RequestOtpRequest
import net.thechance.identity.api.dto.otp.RequestOtpResponse
import net.thechance.identity.api.dto.otp.VerifyOtpRequest
import net.thechance.identity.api.dto.password.ResetPasswordRequest
import net.thechance.identity.api.dto.register.CheckUserExistenceRequest
import net.thechance.identity.api.dto.register.RegisterUserRequest
import net.thechance.identity.api.mapper.toCountryResponses
import net.thechance.identity.api.mapper.toRegisterUserModel
import net.thechance.identity.service.AuthenticationService
import net.thechance.identity.service.RegisterService
import net.thechance.identity.service.ResetPasswordService
import net.thechance.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/identity/authentication")
class IdentityController(
    private val authenticationService: AuthenticationService,
    private val resetPasswordService: ResetPasswordService,
    private val registerService: RegisterService,
    private val userService: UserService
) {
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: AuthRequest
    ): ResponseEntity<AuthResponse> {
        val authResponse = authenticationService.login(
            phoneNumber = request.phoneNumber,
            password = request.password
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

    @PostMapping("/register/request-otp")
    fun requestRegisterOtp(
        @Valid @RequestBody request: RequestOtpRequest,
    ): ResponseEntity<RequestOtpResponse> {
        val response = registerService.requestOtp(
            phoneNumber = request.phoneNumber,
            defaultRegion = request.defaultRegion
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register/verify-otp")
    fun verifyRegisterOtp(
        @Valid @RequestBody request: VerifyOtpRequest,
    ): ResponseEntity<Unit> {
        val response = registerService.verifyOtp(
            otp = request.otp,
            sessionId = UUID.fromString(request.sessionId)
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/register/check-user-existence")
    fun checkUserExistence(
        @Valid @RequestBody checkUserExistenceRequest: CheckUserExistenceRequest
    ): ResponseEntity<Boolean> {
        val response = userService.userExistsByUserName(checkUserExistenceRequest.username)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun registerUser(
        @Valid @RequestBody registerUserRequest: RegisterUserRequest
    ): ResponseEntity<AuthResponse> {
        val authResponse = registerService.registerUser(registerUserRequest.toRegisterUserModel())
        return ResponseEntity.ok(authResponse)
    }

    @GetMapping("/countries")
    fun getSupportedCountries(): ResponseEntity<List<CountryResponse>> {
        val response = authenticationService.getCountries().toCountryResponses()
        return ResponseEntity.ok(response)
    }
}