package net.thechance.identity.mapper

import net.thechance.identity.api.dto.AddressResponse
import net.thechance.identity.api.dto.CreateAddressRequest
import net.thechance.identity.api.dto.UpdateAddressRequest
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
        latitude = this.latitude,
        longitude = this.longitude,
        addressLine = this.addressLine,
        addressType = this.addressType,
        isActive = false
    )
}

fun UpdateAddressRequest.toAddressModel(): AddressModel {
    return AddressModel(
        latitude = this.latitude,
        longitude = this.longitude,
        addressLine = this.addressLine,
        addressType = this.addressType,
        isActive = this.isActive
    )
}