package net.thechance.identity.service.model

data class Address(
    val latitude: Double?,
    val longitude: Double?,
    val addressLine: String?,
    val addressType: String?,
    val isActive: Boolean?,
)
