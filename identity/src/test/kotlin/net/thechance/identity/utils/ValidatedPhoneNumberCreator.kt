package net.thechance.identity.utils

import net.thechance.identity.service.model.ValidatedPhoneNumber

fun createValidatedPhoneNumber(
    phoneNumber: String = "+201122334455",
    regionCode: String = "EG",
    countryCode: String = "20",
    carrierPrefixHeuristic: String = "11",
): ValidatedPhoneNumber {
    return ValidatedPhoneNumber(
        phoneNumber = phoneNumber,
        regionCode = regionCode,
        countryCode = countryCode,
        carrierPrefixHeuristic = carrierPrefixHeuristic,
    )
}
