package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.ManagedUserResponse
import net.thechance.identity.api.dto.PageResponse
import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User
import org.springframework.data.domain.Page

fun User.toProfileResponse(imageBaseUrl: String): ProfileResponse {
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

private fun User.toManagedUserResponse() = ManagedUserResponse(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    lastLoginAt = lastLoginAt,
    lastVisitAt = lastVisitAt,
    status = status
)

fun Page<User>.toUserResponsePage(): PageResponse<ManagedUserResponse> {
    return PageResponse(
        items = this.content.map(User::toManagedUserResponse),
        page = this.number,
        pageSize = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages
    )
}