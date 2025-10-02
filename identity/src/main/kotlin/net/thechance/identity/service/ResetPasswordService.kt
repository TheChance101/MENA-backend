package net.thechance.identity.service

import net.thechance.identity.api.dto.RequestOtpResponse
import net.thechance.identity.api.dto.VerifyOtpResponse
import net.thechance.identity.entity.OtpLog
import net.thechance.identity.exception.OtpExpiredException
import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.UnauthorizedException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResetPasswordService(
    private val phoneNumberValidatorService: PhoneNumberValidatorService,
    private val phoneNumberRateLimitService: PhoneNumberRateLimitService,
    private val otpService: OtpService,
    private val smsService: SmsService,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
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

    fun verifyOtp(otp: String, sessionId: UUID): VerifyOtpResponse {
        otpService.verifyOtp(otp, sessionId)
        return VerifyOtpResponse("OTP verified successfully")
    }

    fun resetPassword(newPassword: String, confirmPassword: String, sessionId: UUID) {
        if (newPassword != confirmPassword) throw PasswordMismatchException()
        val latestOtp = getLatestNotExpiredOtp(sessionId)
        if (!latestOtp.isVerified) throw UnauthorizedException()
        val encodedPassword = passwordEncoder.encode(newPassword)
        userService.updatePasswordByPhoneNumber(latestOtp.phoneNumber, encodedPassword)
        otpService.expireOtpBySessionId(sessionId)
    }

    private fun getLatestNotExpiredOtp(sessionId: UUID): OtpLog {
        try {
            return otpService.getLatestNotExpiredOtpBySessionId(sessionId)
        } catch (_: OtpExpiredException) {
            throw UnauthorizedException()
        }
    }

    private fun checkPhoneNumberExistence(phoneNumber: String) {
        try {
            userService.findByPhoneNumber(phoneNumber)
        } catch (_: Exception) {
            throw UserNotFoundException("User not found")
        }
    }
}