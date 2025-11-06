package net.thechance.identity.service.sms

import org.springframework.stereotype.Service

@Service
class FakeSmsSenderService : SmsSender {
    override fun sendSms(countryCode: String, carrierPrefix: String, phoneNumber: String, message: String) {}
}