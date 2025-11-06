package net.thechance.identity.service.otpGenerator

interface OtpGenerator {
    fun generateOtp(): String
}