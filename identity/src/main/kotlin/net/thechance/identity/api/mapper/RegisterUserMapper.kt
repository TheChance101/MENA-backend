package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.register.RegisterUserRequest
import net.thechance.identity.service.model.RegisterUserModel

fun RegisterUserRequest.toRegisterUserModel(): RegisterUserModel {
    return RegisterUserModel(
        phoneNumber = this.phoneNumber,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        gender = this.gender,
        password = this.password
    )
}