package net.thechance.identity.service

import net.thechance.identity.api.dto.RequestOtpResponse
import net.thechance.identity.api.dto.VerifyOtpResponse
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.springframework.stereotype.Service

@Service
class ResetPasswordService(
    private val phoneNumberValidatorService: PhoneNumberValidatorService,
    private val phoneNumberRateLimitService: PhoneNumberRateLimitService,
    private val otpService: OtpService,
    private val smsService: SmsService,
    private val userRepository: UserRepository
) {
    fun requestOtp(phoneNumber: String, defaultRegion: String): RequestOtpResponse {
        val validatedPhoneNumber = phoneNumberValidatorService.validateAndParse(phoneNumber, defaultRegion)
        checkPhoneNumberExistence(phoneNumber)
        phoneNumberRateLimitService.checkRequestLimit(phoneNumber)
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
}