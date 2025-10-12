package net.thechance.identity.service.otpGenerator

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
@Profile("prod")
class ProductionOtpGeneratorService : OtpGeneratorService {
    override fun generateOtp(): String {
        return Random.nextInt(100000, 1000000).toString()
    }
}