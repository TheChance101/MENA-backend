package net.thechance.identity.api.dto.address

import java.util.UUID

data class AddressResponse(
    val id: UUID,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val isActive: Boolean
)