package net.thechance.identity.mapper

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User
import net.thechance.identity.security.config.IdentityStorageProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

fun User.toResponse(cdnEndpoint: String): ProfileResponse {
    return ProfileResponse(
        id = id.toString(),
        username = username,
        firstName = firstName,
        lastName = lastName,
        imageUrl = "$cdnEndpoint/$imageUrl",
        birthDate = birthDate.toString(),
        gender = gender
    )
}