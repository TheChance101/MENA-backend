package net.thechance.identity.service.phoneNumberValidator

import net.thechance.identity.service.model.ValidatedPhoneNumber

interface PhoneNumberValidatorService {
    /**
     * Parses and validates a phone number string.
     * @param phoneNumberString The phone number string to validate (e.g., "+201122334455").
     * @param defaultRegion The 2-letter ISO country code (e.g., "EG" for Egypt, "SA" for Saudi Arabia).
     */
    fun validateAndParse(phoneNumberString: String, defaultRegion: String): ValidatedPhoneNumber
}