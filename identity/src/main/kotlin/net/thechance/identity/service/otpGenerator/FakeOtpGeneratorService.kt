package net.thechance.identity.service.otpGenerator

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("dev", "stg", "default", "!prod")
class FakeOtpGeneratorService : OtpGeneratorService {
    override fun generateOtp(): String {
        return "000000"
    }
}