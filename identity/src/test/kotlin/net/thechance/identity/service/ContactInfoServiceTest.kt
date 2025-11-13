package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import net.thechance.identity.service.model.ContactInfoModel
import org.junit.Test

class ContactInfoServiceTest {
    private val contactInfoService = ContactInfoService()

    @Test
    fun `getContactInfo() should return contact info`() {
        val result = contactInfoService.getContactInfo()

        assertThat(result).isEqualTo(contactInfo)
    }

    companion object {
        private val contactInfo = ContactInfoModel(
            email = "MENA2025@gmail.com",
            phoneNumber = "+964 770 0000 000",
            facebookAccount = "https://www.facebook.com"
        )
    }
}