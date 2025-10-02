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
import io.mockk.verify
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.PasswordMismatchException
import net.thechance.identity.exception.PasswordNotUpdatedException
import net.thechance.identity.utils.DummyUsers
import org.springframework.security.crypto.password.PasswordEncoder
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

    private val userService: UserService = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val resetPasswordService = ResetPasswordService(
        phoneNumberValidatorService = phoneNumberValidatorService,
        phoneNumberRateLimitService = phoneNumberRateLimitService,
        otpService = otpService,
        smsService = smsService,
        userRepository = userRepository,
        userService = userService,
        passwordEncoder = passwordEncoder
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

    @Test
    fun `resetPassword() should throw PasswordMismatchException when password and confirm password do not match`() {
        assertThrows(PasswordMismatchException::class.java) {
            resetPasswordService.resetPassword(phoneNumber, newPassword, wrongConfirmPassword)
        }
    }

    @Test
    fun `resetPassword() should update password and not throwing exceptions when updatePasswordByPhoneNumber returns true`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true

        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
    }

    @Test
    fun `resetPassword() should call updatePasswordByPhoneNumber one time when called with correct data`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true

        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)

        verify(exactly = 1) { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) }
    }

    @Test
    fun `resetPassword() should call encode one time when called with correct data`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true

        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)

        verify(exactly = 1) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `resetPassword() should throw InvalidCredentialsException when updatePasswordByPhoneNumber throws`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every {
            userService.updatePasswordByPhoneNumber(
                phoneNumber,
                newPassword
            )
        } throws InvalidCredentialsException("")

        assertThrows(InvalidCredentialsException::class.java) {
            resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
        }
    }

    @Test
    fun `resetPassword() should throw PasswordNotUpdatedException when updatePasswordByPhoneNumber returns false`() {
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns false

        assertThrows(PasswordNotUpdatedException::class.java) {
            resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
        }
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

private val phoneNumber = DummyUsers.validUser1.phoneNumber
private val newPassword = DummyUsers.validUser1.password
private val confirmPassword = DummyUsers.validUser1.password
private val wrongConfirmPassword = DummyUsers.validUser2.password
