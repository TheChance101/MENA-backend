package net.thechance.identity.service

import net.thechance.identity.entity.OtpLog
import net.thechance.identity.exception.InvalidOtpException
import net.thechance.identity.exception.OtpExpiredException
import net.thechance.identity.repository.OtpLogRepository
import net.thechance.identity.service.otpGenerator.OtpGeneratorService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class OtpService(
    private val otpGeneratorService: OtpGeneratorService,
    private val otpLogRepository: OtpLogRepository
) {
    fun createOtp(phoneNumber: String): OtpLog {
        expireOldActiveOtpByPhoneNumber(phoneNumber)
        val otp = otpGeneratorService.generateOtp()
        val otpLog = OtpLog(
            phoneNumber = phoneNumber,
            otp = otp,
            expireAt = Instant.now().plusSeconds(OTP_EXPIRY_SECONDS),
        )
        otpLogRepository.save(otpLog)
        return otpLog
    }

    fun verifyOtp(otp: String, sessionId: String) {
        val parsedSessionId = UUID.fromString(sessionId)
        val otpLog = otpLogRepository.findByOtpAndSessionId(otp, parsedSessionId)
            ?: throw InvalidOtpException()
        if (otpLog.isVerified) throw InvalidOtpException()
        if (otpLog.expireAt.isBefore(Instant.now())) throw OtpExpiredException()
        otpLogRepository.verifyOtp(parsedSessionId)
    }

    private fun expireOldActiveOtpByPhoneNumber(phoneNumber: String) {
        otpLogRepository.updateExpirationByPhoneNumber(phoneNumber)
    }

    @Scheduled(cron = "0 0 0 * * *")
    private fun deleteExpiredOtp() {
        otpLogRepository.deleteExpiredOtpBefore(Instant.now())
    }

    companion object {
        private const val OTP_EXPIRY_SECONDS = 3 * 60L
    }
}