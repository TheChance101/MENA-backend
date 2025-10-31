package net.thechance.identity.api.controller.register

import jakarta.validation.Valid
import net.thechance.identity.api.dto.*
import net.thechance.identity.api.dto.register.CheckUserExistenceRequest
import net.thechance.identity.api.dto.register.RegisterUserRequest
import net.thechance.identity.api.mapper.toRegisterUserModel
import net.thechance.identity.service.RegisterService
import net.thechance.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/identity/register")
class RegisterController(
    private val registerService: RegisterService,
    private val userService: UserService
) {

    @PostMapping("/request-otp")
    fun requestOtp(
        @Valid @RequestBody request: RequestOtpRequest,
    ): ResponseEntity<RequestOtpResponse> {
        val response = registerService.requestOtp(
            phoneNumber = request.phoneNumber,
            defaultRegion = request.defaultRegion
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify-otp")
    fun verifyOtp(
        @Valid @RequestBody request: VerifyOtpRequest,
    ): ResponseEntity<Unit> {
        val response = registerService.verifyOtp(
            otp = request.otp,
            sessionId = UUID.fromString(request.sessionId)
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/check-user-existence")
    fun checkUserExistence(
        @Valid @RequestBody checkUserExistenceRequest: CheckUserExistenceRequest
    ): ResponseEntity<Boolean> {
        val response = userService.userExistsByUserName(checkUserExistenceRequest.username)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register-user")
    fun registerUser(
        @Valid @RequestBody registerUserRequest: RegisterUserRequest
    ): ResponseEntity<AuthResponse> {
        val authResponse = registerService.registerUser(registerUserRequest.toRegisterUserModel())
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/add-user-image")
    fun addUserImage(
        @AuthenticationPrincipal userId: UUID,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<UpdateImageResponse> {
        val imageUrl = userService.updateUserImage(userId, file)
        val response = UpdateImageResponse(imageUrl)
        return ResponseEntity.ok(response)
    }

}