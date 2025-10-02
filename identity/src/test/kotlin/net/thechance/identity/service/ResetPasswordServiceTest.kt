package net.thechance.identity.service

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import net.thechance.identity.entity.OtpLog
import net.thechance.identity.entity.User
import net.thechance.identity.exception.FrequentOtpRequestException
import net.thechance.identity.exception.InvalidPhoneNumberException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.model.ValidatedPhoneNumber
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.util.*

class ResetPasswordServiceTest {
    private val phoneNumberValidatorService: PhoneNumberValidatorService = mockk(relaxed = true)
    private val phoneNumberRateLimitService: PhoneNumberRateLimitService = mockk(relaxed = true)
    private val otpService: OtpService = mockk(relaxed = true)
    private val smsService: SmsService = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)

    private val resetPasswordService = ResetPasswordService(
        phoneNumberValidatorService = phoneNumberValidatorService,
        phoneNumberRateLimitService = phoneNumberRateLimitService,
        otpService = otpService,
        smsService = smsService,
        userRepository = userRepository,
    )

    @Test
    fun `requestOtp should throw InvalidPhoneNumberException when user enters wrong phone number`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } throws InvalidPhoneNumberException("")
        assertThrows(InvalidPhoneNumberException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp should throw UserNotFoundException when user enters unregistered phone number`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns null
        assertThrows(UserNotFoundException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp should throw FrequentOtpRequestException when user request otp more than 1 time in a minute`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } throws FrequentOtpRequestException()
        assertThrows(FrequentOtpRequestException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp should throw FrequentOtpRequestException when user request otp more than 5 time in 10 minute`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } throws FrequentOtpRequestException()
        assertThrows(FrequentOtpRequestException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp should send otp to user via sms when limit is not exceeded`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } just runs
        every { otpService.createOtp(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `requestOtp should send otp to user via sms when limit of long term is not exceeded`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } just runs
        every { otpService.createOtp(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `requestOtp should send otp to user via sms when input is valid`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } just runs
        every { otpService.createOtp(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `verifyOtp should be verified when otp is and session id are valid`() {
        every { otpService.verifyOtp(any(), any()) } just runs
        resetPasswordService.verifyOtp(OTP, SESSION_ID)
    }

    companion object {
        private const val PHONE_NUMBER = "+201122334455"
        private const val DEFAULT_REGION = "EG"
        private const val OTP = "000000"
        private val SESSION_ID = UUID.randomUUID().toString()

        private val validatedPhoneNumber = ValidatedPhoneNumber(
            phoneNumber = PHONE_NUMBER,
            regionCode = DEFAULT_REGION,
            countryCode = "20",
            carrierPrefixHeuristic = "11",
        )

        private val user = User(
            phoneNumber = PHONE_NUMBER,
            password = "password",
            firstName = "",
            lastName = "",
            username = "",
            imageUrl = null,
        )

        private val otpLog = OtpLog(
            phoneNumber = PHONE_NUMBER,
            otp = OTP,
            expireAt = Instant.now().plusSeconds(180L),
        )
    }
}