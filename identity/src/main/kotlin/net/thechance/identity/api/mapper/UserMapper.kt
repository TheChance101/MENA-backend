package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User

fun User.toResponse(cdnEndpoint: String): ProfileResponse {
    return ProfileResponse(
        id = id.toString(),
        username = username,
        firstName = firstName,
        lastName = lastName,
        imageUrl = if (imageUrl.isNullOrBlank()) null else "$cdnEndpoint/$imageUrl",
        birthDate = birthDate.toString(),
        gender = gender
    )
}