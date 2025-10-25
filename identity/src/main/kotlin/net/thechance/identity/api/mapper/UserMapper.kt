package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User

fun User.toResponse(): ProfileResponse {
	return ProfileResponse(
		username = username,
		firstName = firstName,
		lastName = lastName,
		profileImageUrl = imageUrl
	)
}