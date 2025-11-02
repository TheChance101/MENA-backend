package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User

fun User.toResponse(imageBaseUrl: String): ProfileResponse {
    val imageUrl1 = if (imageUrl.isNullOrBlank()) {
        null
    } else {
        "$imageBaseUrl/$imageUrl"
    }
    return ProfileResponse(
        id = id.toString(),
        username = username,
        firstName = firstName,
        lastName = lastName,
        imageUrl = imageUrl1,
        birthDate = birthDate.toString(),
        gender = gender
    )
}