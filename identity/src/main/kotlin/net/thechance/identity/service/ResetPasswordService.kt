package net.thechance.identity.service

import net.thechance.identity.api.dto.RequestOtpResponse
import net.thechance.identity.api.dto.VerifyOtpResponse
import net.thechance.identity.entity.OtpLog
import net.thechance.identity.exception.*
import net.thechance.identity.repository.OtpLogRepository
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.otpGenerator.OtpGeneratorService
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class ResetPasswordService(
    private val phoneNumberValidatorService: PhoneNumberValidatorService,
    private val otpGeneratorService: OtpGeneratorService,
    private val smsService: SmsService,
    private val otpLogRepository: OtpLogRepository,
    private val userRepository: UserRepository
) {
    fun requestOtp(phoneNumber: String, defaultRegion: String): RequestOtpResponse {
        val validatedPhoneNumber = phoneNumberValidatorService.validateAndParse(phoneNumber, defaultRegion)
        checkPhoneNumberExistence(phoneNumber)
        checkRequestLimitByPhoneNumber(phoneNumber)
        expireOldActiveOtpByPhoneNumber(phoneNumber)
        val otp = otpGeneratorService.generateOtp()
        val otpLog = OtpLog(
            phoneNumber = phoneNumber,
            otp = otp,
            expireAt = Instant.now().plusSeconds(OTP_EXPIRY_SECONDS),
        )
        otpLogRepository.save(otpLog)
        smsService.sendSms(
            validatedPhoneNumber.countryCode,
            validatedPhoneNumber.carrierPrefixHeuristic,
            validatedPhoneNumber.phoneNumber,
            otp,
        )
        return RequestOtpResponse(otpLog.sessionId.toString())
    }

    fun verifyOtp(phoneNumber: String, otp: String, sessionId: String): VerifyOtpResponse {
        val parsedSessionId = UUID.fromString(sessionId)
        val otpLog = otpLogRepository.findByPhoneNumberAndOtpAndSessionId(phoneNumber, otp, parsedSessionId)
            ?: throw InvalidOtpException()
        if (otpLog.isVerified) throw OtpAlreadyVerifiedException()
        if (otpLog.expireAt.isBefore(Instant.now())) throw OtpExpiredException()
        otpLogRepository.verifyOtp(phoneNumber, parsedSessionId)
        return VerifyOtpResponse("OTP verified successfully")
    }

    private fun checkPhoneNumberExistence(phoneNumber: String) {
        userRepository.findByPhoneNumber(phoneNumber) ?: throw UserNotFoundException()
    }

    private fun expireOldActiveOtpByPhoneNumber(phoneNumber: String) {
        otpLogRepository.updateExpirationByPhoneNumber(phoneNumber)
    }

    private fun checkRequestLimitByPhoneNumber(phoneNumber: String) {
        val pageable = PageRequest.of(0, MAX_RETRIES_PER_SESSION)
        val latestOtpLog = otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber, pageable)
        if (latestOtpLog.isNotEmpty()) {
            val isLastRequestIsFrequent =
                latestOtpLog.first().createdAt > Instant.now().minusSeconds(MIN_OTP_RESEND_GAP_SECONDS)
            val ifWindowLimitExceeded =
                latestOtpLog.last().createdAt > Instant.now().minusSeconds(WINDOW_DURATION_IN_SECONDS)
            if (isLastRequestIsFrequent || ifWindowLimitExceeded) throw FrequentOtpRequestException()
        }
    }

    companion object {
        private const val OTP_EXPIRY_SECONDS = 3 * 60L
        private const val MIN_OTP_RESEND_GAP_SECONDS = 60L
        private const val MAX_RETRIES_PER_SESSION = 5
        private const val WINDOW_DURATION_IN_SECONDS = 10 * 60L
    }
}