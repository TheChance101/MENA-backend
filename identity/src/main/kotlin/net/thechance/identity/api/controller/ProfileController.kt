package net.thechance.identity.api.controller

import jakarta.validation.Valid
import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.api.dto.UpdateProfileRequest
import net.thechance.identity.api.dto.toUserServiceModel
import net.thechance.identity.mapper.toResponse
import net.thechance.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/identity/profile")
class ProfileController(
    private val userService: UserService
) {

    @PostMapping("/me")
    fun updateCurrentUserProfile(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestPart("user") updateProfileRequest: UpdateProfileRequest,
        @RequestPart("file", required = false) file: MultipartFile?,
    ): ResponseEntity<ProfileResponse> {
        val updateUser = userService.updateUserProfile(
            user = updateProfileRequest.toUserServiceModel(userId),
            shouldUpdatedImage = updateProfileRequest.updateImage,
            file = file
        )

        return ResponseEntity.ok(updateUser.toResponse())
    }

    @GetMapping("/me")
    fun getCurrentUserProfile(@AuthenticationPrincipal userId: UUID): ResponseEntity<ProfileResponse> {
        val response = userService.findById(userId).toResponse()
        return ResponseEntity.ok(response)
    }
}