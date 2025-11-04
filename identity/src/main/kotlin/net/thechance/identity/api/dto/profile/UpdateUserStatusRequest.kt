package net.thechance.identity.api.dto.profile

import net.thechance.identity.entity.User

data class UpdateUserStatusRequest(
    val status: User.Status,
)
