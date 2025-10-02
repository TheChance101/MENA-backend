package net.thechance.identity.service.phoneNumberValidator

import net.thechance.identity.exception.InvalidPhoneNumberException
import net.thechance.identity.service.model.ValidatedPhoneNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GooglePhoneNumberValidatorServiceTest {

    private val validatorService = GooglePhoneNumberValidatorService()

    @Test
    fun `validateAndParse() should succeed for a valid international mobile number`() {
        val result = validatorService.validateAndParse(FULL_VALID_PHONE_NUMBER, DEFAULT_REGION)

        assertEquals(result, validatedNumber)
    }

    @Test
    fun `validateAndParse() should succeed for a valid local mobile number with a default region`() {
        val result = validatorService.validateAndParse(VALID_PHONE_NUMBER, DEFAULT_REGION)

        assertEquals(result, validatedNumber)
    }

    @Test
    fun `validateAndParse() should throw exception for an unparseable number`() {
        val exception = assertThrows(InvalidPhoneNumberException::class.java) {
            validatorService.validateAndParse(UNPARSABLE_PHONE_NUMBER, DEFAULT_REGION)
        }

        assertEquals(
            "Could not parse phone number: The string supplied did not seem to be a phone number.",
            exception.message
        )
    }

    @Test
    fun `validateAndParse() should throw exception for a number that is not valid`() {
        val exception = assertThrows(InvalidPhoneNumberException::class.java) {
            validatorService.validateAndParse(FULL_INVALID_PHONE_NUMBER, DEFAULT_REGION)
        }
        assertEquals("Invalid phone number format or non-existent number detected for region EG.", exception.message)
    }

    @Test
    fun `validateAndParse() should throw exception for a valid number that is not a mobile number`() {
        val exception = assertThrows(InvalidPhoneNumberException::class.java) {
            validatorService.validateAndParse(LANDLINE_NUMBER, DEFAULT_REGION)
        }
        assertEquals("Phone number is not a valid mobile number (Type: FIXED_LINE).", exception.message)
    }

    companion object {
        private const val FULL_VALID_PHONE_NUMBER = "+201122334455"
        private const val VALID_PHONE_NUMBER = "01122334455"
        private const val FULL_INVALID_PHONE_NUMBER = "+200022334455"
        private const val UNPARSABLE_PHONE_NUMBER = "Invalid Number"
        private const val LANDLINE_NUMBER = "+20211112222"
        private const val DEFAULT_REGION = "EG"

        private val validatedNumber = ValidatedPhoneNumber(
            "+201122334455",
            "EG",
            "20",
            "11",
        )
    }
}