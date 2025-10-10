package net.thechance.chat.service.model

import net.thechance.chat.entity.ContactUser

data class UserModel(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val imageUrl: String? = null,
)

fun ContactUser.toModel(): UserModel{
    return UserModel(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        imageUrl = this.imageUrl
    )
}
