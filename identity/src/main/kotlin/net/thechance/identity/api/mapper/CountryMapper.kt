package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.auth.CountryResponse
import net.thechance.identity.service.model.LocalizedCountry

fun List<LocalizedCountry>.toCountryResponses() = map(LocalizedCountry::toCountryResponse)

private fun LocalizedCountry.toCountryResponse() = CountryResponse(
    name = name,
    callingCode = callingCode,
    countryCodeName = countryCodeName,
    flagEmoji = flagEmoji
)