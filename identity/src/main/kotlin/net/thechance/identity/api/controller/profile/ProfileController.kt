package net.thechance.identity.api.controller.profile

import jakarta.validation.Valid
import net.thechance.identity.api.dto.password.ChangePasswordRequest
import net.thechance.identity.api.dto.password.ChangePasswordResponse
import net.thechance.identity.api.dto.profile.ProfileResponse
import net.thechance.identity.api.dto.profile.UpdateImageResponse
import net.thechance.identity.api.dto.profile.UpdateProfileRequest
import net.thechance.identity.api.mapper.toProfileResponse
import net.thechance.identity.service.ChangePasswordService
import net.thechance.identity.service.UserService
import net.thechance.identity.service.model.UserServiceModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/identity/profile")
class ProfileController(
    @Value("\${storage.mena.cdn-endpoint}") cdnEndpoint: String,
    @Value("\${identity.resources.profile-image-directory}") profileImageDirectory: String,
    private val userService: UserService,
    private val changePasswordService: ChangePasswordService
) {
    private val imagesBaseUrl: String = "$cdnEndpoint/$profileImageDirectory"

    @PostMapping
    fun updateUserProfile(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody updateProfileRequest: UpdateProfileRequest,
    ): ResponseEntity<ProfileResponse> {
        val userServiceModel = updateProfileRequest.toServiceModel(userId)
        val updatedUser = userService.updateUserProfile(userServiceModel)
        return ResponseEntity.ok(updatedUser.toProfileResponse(imagesBaseUrl))
    }

    @GetMapping
    fun getUserProfile(@AuthenticationPrincipal userId: UUID): ResponseEntity<ProfileResponse> {
        val response = userService.findById(userId).toProfileResponse(imagesBaseUrl)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image")
    fun updateUserImage(
        @AuthenticationPrincipal userId: UUID,
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<UpdateImageResponse> {
        val imageUri = userService.updateUserImage(userId, file)
        val response = UpdateImageResponse(imageUrl = "$imagesBaseUrl$imageUri")
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/image")
    fun deleteUserImage(@AuthenticationPrincipal userId: UUID): ResponseEntity<Unit> {
        userService.deleteUserImage(userId)
        return ResponseEntity.ok().build()
    }

    private fun UpdateProfileRequest.toServiceModel(id: UUID) = UserServiceModel(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        birthDate = LocalDate.parse(birthDate),
        gender = gender
    )

    @PostMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody @Valid request: ChangePasswordRequest
    ): ResponseEntity<ChangePasswordResponse> {
        changePasswordService.changePassword(
            userId = userId,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword,
            confirmPassword = request.confirmPassword
        )
        val response = ChangePasswordResponse("Password changed successfully")
        return ResponseEntity.ok(response)
    }
}