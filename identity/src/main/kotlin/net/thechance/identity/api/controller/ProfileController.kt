package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/profile")
class ProfileController(
	private val userService: UserService,
) {

	@GetMapping("/me")
	fun getCurrentUserProfile(): ResponseEntity<ProfileResponse> {
		val userId = SecurityContextHolder.getContext().authentication.let { UUID.fromString(it.name) }
		val response = userService.findById(userId)
		return ResponseEntity.ok(response)
	}
}