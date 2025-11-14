package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.settings.ContactInfoResponse
import net.thechance.identity.service.model.ContactInfoModel

fun ContactInfoModel.toResponse() = ContactInfoResponse(email = email, phoneNumber = phoneNumber, facebookAccount = facebookAccount )
