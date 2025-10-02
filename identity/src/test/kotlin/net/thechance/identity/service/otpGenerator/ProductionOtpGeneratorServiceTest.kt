package net.thechance.identity.service.otpGenerator

import org.junit.Assert.assertEquals
import org.junit.Test

class ProductionOtpGeneratorServiceTest {
    private val productionOtpGeneratorService = ProductionOtpGeneratorService()

    @Test
    fun `generateOtp() should generate a 6-digit OTP`() {
        val otp = productionOtpGeneratorService.generateOtp()

        assertEquals(6, otp.length)
    }
}