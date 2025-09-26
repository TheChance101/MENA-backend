package net.thechance.identity.service.model

data class ValidatedPhoneNumber(
    /**
     * Full phone number in E.164 format "+[Country Code][National Number]" (e.g., "+201122334455").
     */
    val phoneNumber: String,
    /**
     * (e.g., "EG" for Egypt, "SA" for Saudi Arabia).
     */
    val regionCode: String,
    /**
     * (e.g., "20" for Egypt of phone number "+20xxxxxxxxxx").
     */
    val countryCode: String,
    /**
     * (e.g., "11" of Etisalat for phone number "+2011xxxxxxxx").
     */
    val carrierPrefixHeuristic: String
)
