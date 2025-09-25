package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileController(
	private val userService: UserService
) {

	@GetMapping("/{username}")
	fun getUserProfile(@PathVariable username: String): ResponseEntity<ProfileResponse> {
		val response = userService.findByUsername(username)
		return ResponseEntity.ok(response)
	}
}