package net.thechance.identity.service

import io.mockk.*
import net.thechance.identity.entity.OtpLog
import net.thechance.identity.entity.User
import net.thechance.identity.exception.*
import net.thechance.identity.repository.OtpLogRepository
import net.thechance.identity.repository.UserRepository
import net.thechance.identity.service.model.ValidatedPhoneNumber
import net.thechance.identity.service.otpGenerator.OtpGeneratorService
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.util.UUID

class ResetPasswordServiceTest {
    private val phoneNumberValidatorService: PhoneNumberValidatorService = mockk(relaxed = true)
    private val otpGeneratorService: OtpGeneratorService = mockk(relaxed = true)
    private val smsService: SmsService = mockk(relaxed = true)
    private val otpLogRepository: OtpLogRepository = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)

    private val resetPasswordService = ResetPasswordService(
        phoneNumberValidatorService = phoneNumberValidatorService,
        otpGeneratorService = otpGeneratorService,
        smsService = smsService,
        otpLogRepository = otpLogRepository,
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
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns shortTermFrequentOtpLogs
        assertThrows(FrequentOtpRequestException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp should throw FrequentOtpRequestException when user request otp more than 5 time in 10 minute`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns longTermFrequentOtpLogs
        assertThrows(FrequentOtpRequestException::class.java) {
            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
        }
    }

    @Test
    fun `requestOtp should send otp to user via sms when limit is not exceeded`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns otpLogs
        every { otpLogRepository.updateExpirationByPhoneNumber(any()) } just runs
        every { otpGeneratorService.generateOtp() } returns OTP
        every { otpLogRepository.save(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `requestOtp should send otp to user via sms when limit of long term is not exceeded`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns longTermValidOtpLogs
        every { otpLogRepository.updateExpirationByPhoneNumber(any()) } just runs
        every { otpGeneratorService.generateOtp() } returns OTP
        every { otpLogRepository.save(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `requestOtp should send otp to user via sms when input is valid`() {
        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
        every { userRepository.findByPhoneNumber(any()) } returns user
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns emptyList()
        every { otpLogRepository.updateExpirationByPhoneNumber(any()) } just runs
        every { otpGeneratorService.generateOtp() } returns OTP
        every { otpLogRepository.save(any()) } returns otpLog
        every { smsService.sendSms(any(), any(), any(), any()) } just runs
        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
    }

    @Test
    fun `verifyOtp should throw InvalidOtpException when otp not found`() {
        every { otpLogRepository.findByOtpAndSessionId(any(), any()) } returns null
        assertThrows(InvalidOtpException::class.java) {
            resetPasswordService.verifyOtp(OTP, SESSION_ID)
        }
    }

    @Test
    fun `verifyOtp should throw InvalidOtpException when otp is already verified`() {
        every { otpLogRepository.findByOtpAndSessionId(any(), any()) } returns verifiedOtpLog
        assertThrows(InvalidOtpException::class.java) {
            resetPasswordService.verifyOtp(OTP, SESSION_ID)
        }
    }

    @Test
    fun `verifyOtp should throw OtpExpiredException when otp is expired`() {
        every { otpLogRepository.findByOtpAndSessionId(any(), any()) } returns expiredOtpLog
        assertThrows(OtpExpiredException::class.java) {
            resetPasswordService.verifyOtp(OTP, SESSION_ID)
        }
    }

    @Test
    fun `verifyOtp should be verified when otp is and session id are valid`() {
        every { otpLogRepository.findByOtpAndSessionId(any(), any()) } returns otpLog
        every { otpLogRepository.verifyOtp(any()) } just runs
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

        private val shortTermFrequentOtpLogs = listOf(
            OtpLog(
                phoneNumber = PHONE_NUMBER,
                otp = "000000",
                expireAt = Instant.now().plusSeconds(60L),
                createdAt = Instant.now(),
            ),
            OtpLog(
                phoneNumber = PHONE_NUMBER,
                otp = "000000",
                expireAt = Instant.now().plusSeconds(60L),
                createdAt = Instant.now().minusSeconds(10L),
            )
        )

        private val longTermFrequentOtpLogs = List(5) { index ->
            OtpLog(
                phoneNumber = PHONE_NUMBER,
                otp = "000000",
                expireAt = Instant.now().minusSeconds((5 - index) * 60L),
                createdAt = Instant.now().minusSeconds((4 - index) * 60L),
            )
        }

        private val longTermValidOtpLogs = List(5) { index ->
            OtpLog(
                phoneNumber = PHONE_NUMBER,
                otp = "000000",
                expireAt = Instant.now().minusSeconds((index + 11) * 60L),
                createdAt = Instant.now().minusSeconds((index + 10) * 60L),
            )
        }

        private val otpLogs = listOf(
            OtpLog(
                phoneNumber = PHONE_NUMBER,
                otp = "000000",
                expireAt = Instant.now().plusSeconds(60L),
                createdAt = Instant.now().minusSeconds(120L),
            )
        )

        private val otpLog = OtpLog(
            phoneNumber = PHONE_NUMBER,
            otp = OTP,
            expireAt = Instant.now().plusSeconds(180L),
        )

        private val verifiedOtpLog = OtpLog(
            phoneNumber = PHONE_NUMBER,
            otp = OTP,
            isVerified = true,
            expireAt = Instant.now().plusSeconds(180L),
        )

        private val expiredOtpLog = OtpLog(
            phoneNumber = PHONE_NUMBER,
            otp = OTP,
            expireAt = Instant.now().minusSeconds(180L),
        )
    }
}