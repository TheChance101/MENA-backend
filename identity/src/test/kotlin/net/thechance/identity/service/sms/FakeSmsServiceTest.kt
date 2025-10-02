package net.thechance.identity.service.sms

import org.junit.Test

class FakeSmsServiceTest {
    private val smsService = FakeSmsService()

    @Test
    fun `sendSms should just run`() {
        smsService.sendSms(
            phoneNumber = "+201122334455",
            carrierPrefix = "11",
            countryCode = "20",
            message = "283719"
        )
    }
}