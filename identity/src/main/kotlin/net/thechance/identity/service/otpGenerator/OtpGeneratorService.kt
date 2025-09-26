package net.thechance.identity.service.otpGenerator

interface OtpGeneratorService {
    fun generateOtp(): String
}