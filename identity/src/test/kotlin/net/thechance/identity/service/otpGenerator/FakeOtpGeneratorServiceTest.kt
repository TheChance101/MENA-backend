package net.thechance.identity.service.otpGenerator

import org.junit.Assert.assertEquals
import org.junit.Test

class FakeOtpGeneratorServiceTest {
    private val fakeOtpGeneratorService = FakeOtpGeneratorService()

    @Test
    fun `generateOtp() should generate a 6-digit OTP of zeroes`() {
        val otp = fakeOtpGeneratorService.generateOtp()

        assertEquals("000000", otp)
    }
}