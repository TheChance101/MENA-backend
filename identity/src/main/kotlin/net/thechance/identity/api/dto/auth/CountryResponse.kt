package net.thechance.identity.api.dto.auth

data class CountryResponse(
    val name: String,
    val callingCode: String,
    val countryCodeName: String,
    val flagEmoji: String,
    val phoneNumberRegex: String
)