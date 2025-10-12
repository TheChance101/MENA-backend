package net.thechance.identity.mapper

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User
import net.thechance.identity.mapper.util.formatAsString

fun User.toResponse(): ProfileResponse {
    return ProfileResponse(
        username = username,
        firstName = firstName,
        lastName = lastName,
        imageUrl = imageUrl,
        birthDate = birthDate.formatAsString(),
        gender = gender,
    )
}