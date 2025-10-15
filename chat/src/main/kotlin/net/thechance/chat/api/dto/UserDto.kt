package net.thechance.chat.api.dto

import net.thechance.chat.entity.ContactUser

data class UserDto(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val imageUrl: String? = null,
)

fun ContactUser.toDto(): UserDto{
    return UserDto(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        imageUrl = this.imageUrl
    )
}
