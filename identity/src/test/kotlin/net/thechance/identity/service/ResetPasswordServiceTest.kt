package net.thechance.identity.service

import io.mockk.*
import net.thechance.identity.exception.*
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import net.thechance.identity.utils.createOtpLog
import net.thechance.identity.utils.createUser
import net.thechance.identity.utils.createValidatedPhoneNumber
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.util.UUID

class ResetPasswordServiceTest {
    private val phoneNumberValidatorService: PhoneNumberValidatorService = mockk(relaxed = true)
    private val phoneNumberRateLimitService: PhoneNumberRateLimitService = mockk(relaxed = true)
    private val otpService: OtpService = mockk(relaxed = true)
    private val smsService: SmsService = mockk(relaxed = true)
    private val userService: UserService = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val resetPasswordService = ResetPasswordService(
        phoneNumberValidatorService = phoneNumberValidatorService,
        phoneNumberRateLimitService = phoneNumberRateLimitService,
        otpService = otpService,
        smsService = smsService,
        userService = userService,
        passwordEncoder = passwordEncoder
    )

    @Test
    fun `requestOtp() should throw InvalidPhoneNumberException when user enters wrong phone number`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } throws InvalidPhoneNumberException("")
        assertThrows(InvalidPhoneNumberException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp() should throw UserNotFoundException when user enters unregistered phone number`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userService.findByPhoneNumber(any()) } throws UserNotFoundException("")
        assertThrows(UserNotFoundException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp() should throw FrequentOtpRequestException when user request otp more than 1 time in a minute`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userService.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } throws FrequentOtpRequestException()
        assertThrows(FrequentOtpRequestException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp() should throw FrequentOtpRequestException when user request otp more than 5 time in 10 minute`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userService.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } throws FrequentOtpRequestException()
        assertThrows(FrequentOtpRequestException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp() should send otp to user via sms when limit is not exceeded`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userService.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } just runs
        every { otpService.createOtp(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `requestOtp() should send otp to user via sms when limit of long term is not exceeded`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userService.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } just runs
        every { otpService.createOtp(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `requestOtp() should send otp to user via sms when input is valid`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userService.findByPhoneNumber(any()) } returns user
        every { phoneNumberRateLimitService.checkRequestLimit(any()) } just runs
        every { otpService.createOtp(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `verifyOtp() should be verified when otp is and session id are valid`() {
        every { otpService.verifyOtp(any(), any()) } just runs
        resetPasswordService.verifyOtp(OTP, SESSION_ID)
    }

    @Test
    fun `resetPassword() should throw UnauthorizedException when session id not matches with the otp session id`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns otpLog

        assertThrows(UnauthorizedException::class.java) {
            resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, WRONG_SESSION_ID)
        }
    }

    @Test
    fun `resetPassword() should throw UnauthorizedException when otp not verified`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns otpLog

        assertThrows(UnauthorizedException::class.java) {
            resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)
        }
    }

    @Test
    fun `resetPassword() should throw UnauthorizedException when otp expired`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } throws OtpExpiredException()

        assertThrows(UnauthorizedException::class.java) {
            resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)
        }
    }

    @Test
    fun `resetPassword() should throw PasswordMismatchException when password and confirm password do not match`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns verifiedOtpLog

        assertThrows(PasswordMismatchException::class.java) {
            resetPasswordService.resetPassword(NEW_PASSWORD, WRONG_CONFIRM_PASSWORD, SESSION_ID)
        }
    }

    @Test
    fun `resetPassword() should update password and not throwing exceptions when updatePasswordByPhoneNumber returns true`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns verifiedOtpLog
        every { passwordEncoder.encode(any()) } returns NEW_PASSWORD
        every { userService.updatePasswordByPhoneNumber(PHONE_NUMBER, NEW_PASSWORD) } just runs

        resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)
    }

    @Test
    fun `resetPassword() should call updatePasswordByPhoneNumber one time when called with correct data`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns verifiedOtpLog
        every { passwordEncoder.encode(any()) } returns NEW_PASSWORD
        every { userService.updatePasswordByPhoneNumber(PHONE_NUMBER, NEW_PASSWORD) } just runs

        resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)

        verify(exactly = 1) { userService.updatePasswordByPhoneNumber(PHONE_NUMBER, NEW_PASSWORD) }
    }

    @Test
    fun `resetPassword() should call encode one time when called with correct data`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns verifiedOtpLog
        every { passwordEncoder.encode(any()) } returns NEW_PASSWORD
        every { userService.updatePasswordByPhoneNumber(PHONE_NUMBER, NEW_PASSWORD) } just runs

        resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)

        verify(exactly = 1) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `resetPassword() should throw InvalidCredentialsException when updatePasswordByPhoneNumber throws`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns verifiedOtpLog
        every { passwordEncoder.encode(any()) } returns NEW_PASSWORD
        every {
            userService.updatePasswordByPhoneNumber(
                PHONE_NUMBER,
                NEW_PASSWORD
            )
        } throws InvalidCredentialsException("")

        assertThrows(InvalidCredentialsException::class.java) {
            resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)
        }
    }

    @Test
    fun `resetPassword() should throw PasswordNotUpdatedException when updatePasswordByPhoneNumber throws`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(any()) } returns verifiedOtpLog
        every { passwordEncoder.encode(any()) } returns NEW_PASSWORD
        every {
            userService.updatePasswordByPhoneNumber(
                PHONE_NUMBER,
                NEW_PASSWORD
            )
        } throws PasswordNotUpdatedException()

        assertThrows(PasswordNotUpdatedException::class.java) {
            resetPasswordService.resetPassword(NEW_PASSWORD, CONFIRM_PASSWORD, SESSION_ID)
        }
    }

    companion object {
        private const val PHONE_NUMBER = "+201122334455"
        private const val DEFAULT_REGION = "EG"
        private const val OTP = "000000"
        private val SESSION_ID = UUID.fromString("1b3ed35d-94b7-45e4-974c-9da921a27d1c")
        private val WRONG_SESSION_ID = UUID.fromString("1b3ed35d-94b7-4599-974c-9da921a27d1c")

        private const val NEW_PASSWORD = "test12300"
        private const val CONFIRM_PASSWORD = "test12300"
        private const val WRONG_CONFIRM_PASSWORD = "test12301"

        private val validatedPhoneNumber = createValidatedPhoneNumber()
        private val user = createUser(phoneNumber = this.PHONE_NUMBER)
        private val otpLog = createOtpLog(
            otp = OTP,
            expireAt = Instant.now().plusSeconds(180L),
        )
        private val verifiedOtpLog = createOtpLog(
            otp = OTP,
            isVerified = true,
            expireAt = Instant.now().plusSeconds(180L),
        )
    }
}