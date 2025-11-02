package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.register.RegisterUserRequest
import net.thechance.identity.service.model.RegisterUserModel
import java.util.UUID

fun RegisterUserRequest.toRegisterUserModel(): RegisterUserModel {
    return RegisterUserModel(
        phoneNumber = phoneNumber,
        username = username,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        gender = gender,
        password = password,
        sessionId = UUID.fromString(sessionId)
    )
}