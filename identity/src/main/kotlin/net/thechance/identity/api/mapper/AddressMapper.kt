package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.address.AddressResponse
import net.thechance.identity.api.dto.address.CreateAddressRequest
import net.thechance.identity.api.dto.address.UpdateAddressRequest
import net.thechance.identity.entity.Address
import net.thechance.identity.service.model.Address as AddressModel

fun Address.toResponse(): AddressResponse {
    return AddressResponse(
        id = id,
        latitude = latitude,
        longitude = longitude,
        addressLine = addressLine,
        addressType = addressType,
        isActive = isActive
    )
}

fun CreateAddressRequest.toAddressModel(): AddressModel {
    return AddressModel(
        latitude = latitude,
        longitude = longitude,
        addressLine = addressLine,
        addressType = addressType,
        isActive = false
    )
}

fun UpdateAddressRequest.toAddressModel(): AddressModel {
    return AddressModel(
        latitude = latitude,
        longitude = longitude,
        addressLine = addressLine,
        addressType = addressType,
        isActive = isActive
    )
}