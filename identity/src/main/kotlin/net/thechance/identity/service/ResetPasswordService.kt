package net.thechance.identity.service

import net.thechance.identity.api.dto.RequestOtpResponse
import net.thechance.identity.api.dto.VerifyOtpResponse
import net.thechance.identity.exception.*
import net.thechance.identity.repository.OtpLogRepository
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ResetPasswordService(
    private val phoneNumberValidatorService: PhoneNumberValidatorService,
    private val otpService: OtpService,
    private val smsService: SmsService,
    private val otpLogRepository: OtpLogRepository,
    private val userRepository: UserRepository
) {
    fun requestOtp(phoneNumber: String, defaultRegion: String): RequestOtpResponse {
        val validatedPhoneNumber = phoneNumberValidatorService.validateAndParse(phoneNumber, defaultRegion)
        checkPhoneNumberExistence(phoneNumber)
        checkRequestLimitByPhoneNumber(phoneNumber)
        val otpLog = otpService.createOtp(validatedPhoneNumber.phoneNumber)
        smsService.sendSms(
            validatedPhoneNumber.countryCode,
            validatedPhoneNumber.carrierPrefixHeuristic,
            validatedPhoneNumber.phoneNumber,
            otpLog.otp,
        )
        return RequestOtpResponse(otpLog.sessionId.toString())
    }

    fun verifyOtp(otp: String, sessionId: String): VerifyOtpResponse {
        otpService.verifyOtp(otp, sessionId)
        return VerifyOtpResponse("OTP verified successfully")
    }

    private fun checkPhoneNumberExistence(phoneNumber: String) {
        userRepository.findByPhoneNumber(phoneNumber) ?: throw UserNotFoundException("User not found")
    }

    private fun checkRequestLimitByPhoneNumber(phoneNumber: String) {
        val pageable = PageRequest.of(0, MAX_RETRIES_PER_WINDOW)
        val latestOtpLog = otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber, pageable)
        if (latestOtpLog.isNotEmpty()) {
            val isLastRequestIsFrequent =
                latestOtpLog.first().createdAt > Instant.now().minusSeconds(MIN_OTP_RESEND_GAP_SECONDS)
            val ifWindowLimitExceeded =
                latestOtpLog.size == MAX_RETRIES_PER_WINDOW && latestOtpLog.last().createdAt > Instant.now().minusSeconds(WINDOW_DURATION_IN_SECONDS)
            if (isLastRequestIsFrequent || ifWindowLimitExceeded) throw FrequentOtpRequestException()
        }
    }

    companion object {
        private const val MIN_OTP_RESEND_GAP_SECONDS = 60L
        private const val MAX_RETRIES_PER_WINDOW = 5
        private const val WINDOW_DURATION_IN_SECONDS = 10 * 60L
    }
}