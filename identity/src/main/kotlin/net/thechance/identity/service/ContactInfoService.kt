package net.thechance.identity.service

import net.thechance.identity.service.model.ContactInfoModel
import org.springframework.stereotype.Service

@Service
class ContactInfoService {
    fun getContactInfo(): ContactInfoModel {
        return ContactInfoModel(
            email = EMAIL,
            phoneNumber = PHONE_NUMBER,
            facebookAccount = FACEBOOK_ACCOUNT
        )
    }

    companion object {
        private const val EMAIL = "MENA2025@gmail.com"
        private const val PHONE_NUMBER = "+964 770 0000 000"
        private const val FACEBOOK_ACCOUNT = "https://www.facebook.com"
    }
}
