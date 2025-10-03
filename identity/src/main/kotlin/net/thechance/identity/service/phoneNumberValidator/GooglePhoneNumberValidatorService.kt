package net.thechance.identity.service.phoneNumberValidator

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import net.thechance.identity.exception.InvalidPhoneNumberException
import net.thechance.identity.service.model.ValidatedPhoneNumber
import org.springframework.stereotype.Service

@Service
class GooglePhoneNumberValidatorService : PhoneNumberValidatorService {
    private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    override fun validateAndParse(phoneNumberString: String, defaultRegion: String): ValidatedPhoneNumber {
        val parsedNumber: Phonenumber.PhoneNumber
        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumberString, defaultRegion.uppercase())
        } catch (e: NumberParseException) {
            throw InvalidPhoneNumberException("Could not parse phone number: ${e.message}")
        }

        if (!phoneNumberUtil.isValidNumber(parsedNumber)) {
            throw InvalidPhoneNumberException("Invalid phone number format or non-existent number detected for region ${defaultRegion.uppercase()}.")
        }

        val numberType = phoneNumberUtil.getNumberType(parsedNumber)
        if (numberType != PhoneNumberUtil.PhoneNumberType.MOBILE) {
            throw InvalidPhoneNumberException("Phone number is not a valid mobile number (Type: $numberType).")
        }

        val e164Format = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
        val countryCode = parsedNumber.countryCode.toString()
        val regionCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber)

        val nationalNumberString = parsedNumber.nationalNumber.toString()
        val carrierPrefix = if (nationalNumberString.length >= 3) {
            nationalNumberString.substring(0, 3)
        } else {
            nationalNumberString
        }

        return ValidatedPhoneNumber(
            phoneNumber = e164Format,
            countryCode = countryCode,
            regionCode = regionCode,
            carrierPrefixHeuristic = carrierPrefix
        )
    }
}