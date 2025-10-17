package net.thechance.identity.mapper

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User

fun User.toResponse(): ProfileResponse {
    return ProfileResponse(
        id = id.toString(),
        username = username,
        firstName = firstName,
        lastName = lastName,
        imageUrl = imageUrl,
        birthDate = birthDate.toString(),
        gender = gender
    )
}