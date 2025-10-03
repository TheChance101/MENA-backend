//package net.thechance.identity.service
//
//import io.mockk.*
//import net.thechance.identity.entity.OtpLog
//import net.thechance.identity.entity.User
//import net.thechance.identity.exception.*
//import net.thechance.identity.repository.OtpLogRepository
//import net.thechance.identity.repository.UserRepository
//import net.thechance.identity.service.model.ValidatedPhoneNumber
//import net.thechance.identity.service.otpGenerator.OtpGeneratorService
//import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
//import net.thechance.identity.service.sms.SmsService
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import net.thechance.identity.exception.InvalidCredentialsException
//import net.thechance.identity.exception.PasswordMismatchException
//import net.thechance.identity.exception.PasswordNotUpdatedException
//import net.thechance.identity.utils.DummyUsers
//import org.junit.Assert.assertThrows
//import org.junit.Test
//import org.springframework.security.crypto.password.PasswordEncoder
//import java.time.Instant
//import java.util.UUID
//
//class ResetPasswordServiceTest {
//    private val userService: UserService = mockk(relaxed = true)
//    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
//    private val phoneNumberValidatorService: PhoneNumberValidatorService = mockk(relaxed = true)
//    private val otpGeneratorService: OtpGeneratorService = mockk(relaxed = true)
//    private val smsService: SmsService = mockk(relaxed = true)
//    private val otpLogRepository: OtpLogRepository = mockk(relaxed = true)
//    private val userRepository: UserRepository = mockk(relaxed = true)
//
//    private val resetPasswordService = ResetPasswordService(
//        userService = userService,
//        passwordEncoder = passwordEncoder
//        phoneNumberValidatorService = phoneNumberValidatorService,
//        otpGeneratorService = otpGeneratorService,
//        smsService = smsService,
//        otpLogRepository = otpLogRepository,
//        userRepository = userRepository,
//    )
//
//    @Test
//    fun `resetPassword() should throw PasswordMismatchException when password and confirm password do not match`() {
//        assertThrows(PasswordMismatchException::class.java) {
//            resetPasswordService.resetPassword(phoneNumber, newPassword, wrongConfirmPassword)
//    fun `requestOtp should throw InvalidPhoneNumberException when user enters wrong phone number`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } throws InvalidPhoneNumberException("")
//        assertThrows(InvalidPhoneNumberException::class.java) {
//            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//        }
//    }
//
//    @Test
//    fun `requestOtp should throw UserNotFoundException when user enters unregistered phone number`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
//        every { userRepository.findByPhoneNumber(any()) } returns null
//        assertThrows(UserNotFoundException::class.java) {
//            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//        }
//    }
//
//    @Test
//    fun `resetPassword() should update password and not throwing exceptions when updatePasswordByPhoneNumber returns true`() {
//        every { passwordEncoder.encode(any()) } returns newPassword
//        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true
//
//        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
//    fun `requestOtp should throw FrequentOtpRequestException when user request otp more than 1 time in a minute`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
//        every { userRepository.findByPhoneNumber(any()) } returns user
//        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns shortTermFrequentOtpLogs
//        assertThrows(FrequentOtpRequestException::class.java) {
//            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//        }
//    }
//
//    @Test
//    fun `resetPassword() should call updatePasswordByPhoneNumber one time when called with correct data`() {
//        every { passwordEncoder.encode(any()) } returns newPassword
//        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true
//
//        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
//
//        verify(exactly = 1) { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) }
//    fun `requestOtp should throw FrequentOtpRequestException when user request otp more than 5 time in 10 minute`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
//        every { userRepository.findByPhoneNumber(any()) } returns user
//        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns longTermFrequentOtpLogs
//        assertThrows(FrequentOtpRequestException::class.java) {
//            resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//        }
//    }
//
//    @Test
//    fun `resetPassword() should call encode one time when called with correct data`() {
//        every { passwordEncoder.encode(any()) } returns newPassword
//        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns true
//
//        resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
//
//        verify(exactly = 1) { passwordEncoder.encode(any()) }
//    fun `requestOtp should send otp to user via sms when limit is not exceeded`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
//        every { userRepository.findByPhoneNumber(any()) } returns user
//        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns otpLogs
//        every { otpLogRepository.updateExpirationByPhoneNumber(any()) } just runs
//        every { otpGeneratorService.generateOtp() } returns OTP
//        every { otpLogRepository.save(any()) } returns otpLog
//        every { smsService.sendSms(any(), any(), any(), any()) } just runs
//        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//    }
//
//    @Test
//    fun `resetPassword() should throw InvalidCredentialsException when updatePasswordByPhoneNumber throws`() {
//        every { passwordEncoder.encode(any()) } returns newPassword
//        every {
//            userService.updatePasswordByPhoneNumber(
//                phoneNumber,
//                newPassword
//            )
//        } throws InvalidCredentialsException("")
//
//        assertThrows(InvalidCredentialsException::class.java) {
//            resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
//        }
//    fun `requestOtp should send otp to user via sms when limit of long term is not exceeded`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
//        every { userRepository.findByPhoneNumber(any()) } returns user
//        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns longTermValidOtpLogs
//        every { otpLogRepository.updateExpirationByPhoneNumber(any()) } just runs
//        every { otpGeneratorService.generateOtp() } returns OTP
//        every { otpLogRepository.save(any()) } returns otpLog
//        every { smsService.sendSms(any(), any(), any(), any()) } just runs
//        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//    }
//
//    @Test
//    fun `resetPassword() should throw PasswordNotUpdatedException when updatePasswordByPhoneNumber returns false`() {
//        every { passwordEncoder.encode(any()) } returns newPassword
//        every { userService.updatePasswordByPhoneNumber(phoneNumber, newPassword) } returns false
//    fun `requestOtp should send otp to user via sms when input is valid`() {
//        every { phoneNumberValidatorService.validateAndParse(any(), any()) } returns validatedPhoneNumber
//        every { userRepository.findByPhoneNumber(any()) } returns user
//        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns emptyList()
//        every { otpLogRepository.updateExpirationByPhoneNumber(any()) } just runs
//        every { otpGeneratorService.generateOtp() } returns OTP
//        every { otpLogRepository.save(any()) } returns otpLog
//        every { smsService.sendSms(any(), any(), any(), any()) } just runs
//        resetPasswordService.requestOtp(PHONE_NUMBER, DEFAULT_REGION)
//    }
//
//    @Test
//    fun `verifyOtp should throw InvalidOtpException when otp not found`() {
//        every { otpLogRepository.findByPhoneNumberAndOtpAndSessionId(any(), any(), any()) } returns null
//        assertThrows(InvalidOtpException::class.java) {
//            resetPasswordService.verifyOtp(PHONE_NUMBER, OTP, SESSION_ID)
//        }
//    }
//
//    @Test
//    fun `verifyOtp should throw InvalidOtpException when otp is already verified`() {
//        every { otpLogRepository.findByPhoneNumberAndOtpAndSessionId(any(), any(), any()) } returns verifiedOtpLog
//        assertThrows(InvalidOtpException::class.java) {
//            resetPasswordService.verifyOtp(PHONE_NUMBER, OTP, SESSION_ID)
//        }
//    }
//
//    @Test
//    fun `verifyOtp should throw OtpExpiredException when otp is expired`() {
//        every { otpLogRepository.findByPhoneNumberAndOtpAndSessionId(any(), any(), any()) } returns expiredOtpLog
//        assertThrows(OtpExpiredException::class.java) {
//            resetPasswordService.verifyOtp(PHONE_NUMBER, OTP, SESSION_ID)
//        }
//    }
//
//        assertThrows(PasswordNotUpdatedException::class.java) {
//            resetPasswordService.resetPassword(phoneNumber, newPassword, confirmPassword)
//        }
//    }
//}
//    @Test
//    fun `verifyOtp should be verified when otp is and session id are valid`() {
//        every { otpLogRepository.findByPhoneNumberAndOtpAndSessionId(any(), any(), any()) } returns otpLog
//        every { otpLogRepository.verifyOtp(any(), any()) } just runs
//        resetPasswordService.verifyOtp(PHONE_NUMBER, OTP, SESSION_ID)
//    }
//
//    companion object {
//        private const val PHONE_NUMBER = "+201122334455"
//        private const val DEFAULT_REGION = "EG"
//        private const val OTP = "000000"
//        private val SESSION_ID = UUID.randomUUID().toString()
//
//        private val validatedPhoneNumber = ValidatedPhoneNumber(
//            phoneNumber = PHONE_NUMBER,
//            regionCode = DEFAULT_REGION,
//            countryCode = "20",
//            carrierPrefixHeuristic = "11",
//        )
//
//        private val user = User(
//            phoneNumber = PHONE_NUMBER,
//            password = "password",
//        )
//
//        private val shortTermFrequentOtpLogs = listOf(
//            OtpLog(
//                phoneNumber = PHONE_NUMBER,
//                otp = "000000",
//                expireAt = Instant.now().plusSeconds(60L),
//                createdAt = Instant.now(),
//            ),
//            OtpLog(
//                phoneNumber = PHONE_NUMBER,
//                otp = "000000",
//                expireAt = Instant.now().plusSeconds(60L),
//                createdAt = Instant.now().minusSeconds(10L),
//            )
//        )
//
//        private val longTermFrequentOtpLogs = List(5) { index ->
//            OtpLog(
//                phoneNumber = PHONE_NUMBER,
//                otp = "000000",
//                expireAt = Instant.now().minusSeconds((5 - index) * 60L),
//                createdAt = Instant.now().minusSeconds((4 - index) * 60L),
//            )
//        }
//
//        private val longTermValidOtpLogs = List(5) { index ->
//            OtpLog(
//                phoneNumber = PHONE_NUMBER,
//                otp = "000000",
//                expireAt = Instant.now().minusSeconds((index + 11) * 60L),
//                createdAt = Instant.now().minusSeconds((index + 10) * 60L),
//            )
//        }
//
//private val phoneNumber = DummyUsers.validUser1.phoneNumber
//private val newPassword = DummyUsers.validUser1.password
//private val confirmPassword = DummyUsers.validUser1.password
//private val wrongConfirmPassword = DummyUsers.validUser2.password
//        private val otpLogs = listOf(
//            OtpLog(
//                phoneNumber = PHONE_NUMBER,
//                otp = "000000",
//                expireAt = Instant.now().plusSeconds(60L),
//                createdAt = Instant.now().minusSeconds(120L),
//            )
//        )
//
//        private val otpLog = OtpLog(
//            phoneNumber = PHONE_NUMBER,
//            otp = OTP,
//            expireAt = Instant.now().plusSeconds(180L),
//        )
//
//        private val verifiedOtpLog = OtpLog(
//            phoneNumber = PHONE_NUMBER,
//            otp = OTP,
//            isVerified = true,
//            expireAt = Instant.now().plusSeconds(180L),
//        )
//
//        private val expiredOtpLog = OtpLog(
//            phoneNumber = PHONE_NUMBER,
//            otp = OTP,
//            expireAt = Instant.now().minusSeconds(180L),
//        )
//    }
//}