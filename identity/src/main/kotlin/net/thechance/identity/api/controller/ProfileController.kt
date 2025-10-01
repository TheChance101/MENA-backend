package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.GeneralResponse
import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.mapper.toResponse
import net.thechance.identity.service.UpdateUserProfileImageService
import net.thechance.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/identity/profile")
class ProfileController(
    private val userService: UserService,
    private val updateUserProfileImageService: UpdateUserProfileImageService
) {

    @GetMapping("/me")
    fun getCurrentUserProfile(@AuthenticationPrincipal userId: UUID): ResponseEntity<ProfileResponse> {
        val response = userService.findById(userId).toResponse()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image")
    fun uploadProfileImage(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<GeneralResponse> {
        updateUserProfileImageService(userId, file)
        val response = GeneralResponse("Profile image uploaded successfully")
        return ResponseEntity.ok(response)
    }
}