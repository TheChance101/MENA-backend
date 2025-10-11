package net.thechance.wallet.api.dto.user

import net.thechance.wallet.entity.user.WalletUser

data class UserDetailsResponse(
    val name: String,
    val imageUrl: String?,
)

fun WalletUser.toUserDetailsResponse(): UserDetailsResponse =
    UserDetailsResponse(
        name = "$firstName $lastName",
        imageUrl = imageUrl
    )
