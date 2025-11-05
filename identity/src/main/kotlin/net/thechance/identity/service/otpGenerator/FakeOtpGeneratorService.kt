package net.thechance.identity.service.otpGenerator

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!prod")
class FakeOtpGeneratorService : OtpGenerator {
    override fun generateOtp(): String {
        return "000000"
    }
}