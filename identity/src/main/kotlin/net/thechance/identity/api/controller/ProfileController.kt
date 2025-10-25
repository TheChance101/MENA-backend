package net.thechance.identity.api.controller

import jakarta.validation.Valid
import net.thechance.identity.api.dto.UpdateProfileRequest
import net.thechance.identity.api.dto.DeleteImageResponse
import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.api.dto.UpdateImageResponse
import net.thechance.identity.mapper.toResponse
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
    private val userService: UserService,
    @param:Value("storage.mena.cdn-endpoint") private val cdnEndpoint: String,
) {

    @PostMapping
    fun updateUserProfile(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody updateProfileRequest: UpdateProfileRequest,
    ): ResponseEntity<ProfileResponse> {
        val userServiceModel = updateProfileRequest.toServiceModel(userId)
        val updatedUser = userService.updateUserProfile(userServiceModel)
        return ResponseEntity.ok(updatedUser.toResponse(cdnEndpoint))
    }

    @GetMapping
    fun getUserProfile(@AuthenticationPrincipal userId: UUID): ResponseEntity<ProfileResponse> {
        val response = userService.findById(userId).toResponse(cdnEndpoint)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image")
    fun updateUserImage(
        @AuthenticationPrincipal userId: UUID,
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<UpdateImageResponse> {
        val imageUrl = userService.updateUserImage(userId, file)
        val response = UpdateImageResponse(imageUrl)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/image")
    fun deleteUserImage(@AuthenticationPrincipal userId: UUID): ResponseEntity<DeleteImageResponse> {
        userService.deleteUserImage(userId)
        val response = DeleteImageResponse("Image deleted successfully")
        return ResponseEntity.ok(response)
    }

    private fun UpdateProfileRequest.toServiceModel(id: UUID) = UserServiceModel(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        birthDate = LocalDate.parse(birthDate),
        gender = gender
    )
}