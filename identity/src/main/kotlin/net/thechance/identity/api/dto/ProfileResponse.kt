package net.thechance.identity.api.dto

data class ProfileResponse(
	val username: String,
	val firstName: String,
	val lastName: String,
	val profileImageUrl: String?
)