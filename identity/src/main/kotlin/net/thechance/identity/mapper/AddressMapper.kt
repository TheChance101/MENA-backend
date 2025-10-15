package net.thechance.identity.mapper

import net.thechance.identity.api.dto.AddressResponse
import net.thechance.identity.entity.Address

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