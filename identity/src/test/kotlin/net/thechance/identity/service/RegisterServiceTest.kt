package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import net.thechance.identity.entity.RefreshToken
import net.thechance.identity.entity.User
import net.thechance.identity.exception.OtpExpiredException
import net.thechance.identity.exception.UnauthorizedException
import net.thechance.identity.exception.UserAlreadyExistsException
import net.thechance.identity.security.JwtService
import net.thechance.identity.service.model.RegisterUserModel
import net.thechance.identity.service.model.ValidatedPhoneNumber
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidator
import net.thechance.identity.service.sms.SmsSender
import net.thechance.identity.utils.createOtpLog
import org.junit.Assert.assertThrows
import org.junit.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.*

class RegisterServiceTest {
    private val phoneNumberValidator: PhoneNumberValidator = mockk(relaxed = true)
    private val otpService: OtpService = mockk(relaxed = true)
    private val smsSender: SmsSender = mockk(relaxed = true)
    private val userService: UserService = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val jwtService: JwtService = mockk(relaxed = true)
    private val refreshTokenService: RefreshTokenService = mockk(relaxed = true)

    private val registerService: RegisterService = RegisterService(
        phoneNumberValidator = phoneNumberValidator,
        otpService = otpService,
        smsSender = smsSender,
        userService = userService,
        passwordEncoder = passwordEncoder,
        jwtService = jwtService,
        refreshTokenService = refreshTokenService
    )

    @Test
    fun `requestOtp should send OTP when phone number is valid and does not exist`() {
        every { phoneNumberValidator.validateAndParse(any(), any()) } returns dummyValidatedPhone
        every { userService.userExistsByPhoneNumber(DUMMY_PHONE_NUMBER) } returns false
        every { otpService.createOtp(DUMMY_PHONE_NUMBER) } returns verifiedOtpLog

        val response = registerService.requestOtp(DUMMY_PHONE_NUMBER, DEFAULT_REGION)

        assertThat(response.sessionId).isEqualTo(sessionId.toString())
    }

    @Test
    fun `requestOtp should call send sms and createOtP when phone number is valid and does not exist`() {
        every { phoneNumberValidator.validateAndParse(any(), any()) } returns dummyValidatedPhone
        every { userService.userExistsByPhoneNumber(any()) } returns false
        every { otpService.createOtp(any()) } returns verifiedOtpLog
        every { smsSender.sendSms(any(), any(), any(), any()) } just runs

        registerService.requestOtp(DUMMY_PHONE_NUMBER, DEFAULT_REGION)

        verify(exactly = 1) { phoneNumberValidator.validateAndParse(DUMMY_PHONE_NUMBER, DEFAULT_REGION) }
        verify(exactly = 1) { userService.userExistsByPhoneNumber(DUMMY_PHONE_NUMBER) }
        verify(exactly = 1) { otpService.createOtp(DUMMY_PHONE_NUMBER) }
        verify(exactly = 1) {
            smsSender.sendSms(
                dummyValidatedPhone.countryCode,
                dummyValidatedPhone.carrierPrefixHeuristic,
                dummyValidatedPhone.phoneNumber,
                verifiedOtpLog.otp
            )
        }
    }

    @Test
    fun `requestOtp should throw UserAlreadyExistsException when phone number exists`() {
        every { phoneNumberValidator.validateAndParse(any(), any()) } returns dummyValidatedPhone
        every { userService.userExistsByPhoneNumber(any()) } returns true

        assertThrows(UserAlreadyExistsException::class.java) {
            registerService.requestOtp(DUMMY_PHONE_NUMBER, DEFAULT_REGION)
        }
        verify(exactly = 1) { phoneNumberValidator.validateAndParse(DUMMY_PHONE_NUMBER, DEFAULT_REGION) }
        verify(exactly = 1) { userService.userExistsByPhoneNumber(DUMMY_PHONE_NUMBER) }
        verify(exactly = 0) { otpService.createOtp(any()) }
        verify(exactly = 0) { smsSender.sendSms(any(), any(), any(), any()) }
    }

    @Test
    fun `verifyOtp should call otpService to verify when called`() {
        every { otpService.verifyOtp(any(), any()) } just runs

        registerService.verifyOtp(OTP, sessionId)

        verify(exactly = 1) { otpService.verifyOtp(OTP, sessionId) }
    }

    @Test
    fun `registerUser should throw UnauthorizedException when otp not verified`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(sessionId) } returns notVerifiedOtpLog

        assertThrows(UnauthorizedException::class.java) {
            registerService.registerUser(registerModel)
        }
    }

    @Test
    fun `registerUser should throw OtpExpiredException when otp is expired`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(sessionId) } throws OtpExpiredException()

        assertThrows(OtpExpiredException::class.java) {
            registerService.registerUser(registerModel)
        }
    }

    @Test
    fun `registerUser should save user and return auth tokens when user does not exist`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(sessionId) } returns verifiedOtpLog
        every { userService.userExistsByUserName(any()) } returns false
        every { userService.userExistsByPhoneNumber(any()) } returns false
        every { passwordEncoder.encode(any()) } returns ENCODED_PASSWORD
        every { userService.saveUser(any()) } returns savedUser
        every { jwtService.generateToken(savedUser) } returns ACCESS_TOKEN
        every { refreshTokenService.createRefreshToken(savedUser) } returns refreshToken

        val authResponse = registerService.registerUser(registerModel)

        assertThat(authResponse.accessToken).isEqualTo(ACCESS_TOKEN)
        assertThat(authResponse.refreshToken).isEqualTo(refreshToken.refreshToken)
    }

    @Test
    fun `registerUser should throw UserAlreadyExistsException when username is taken`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(sessionId) } returns verifiedOtpLog
        every { userService.userExistsByUserName(any()) } returns true

        assertThrows(UserAlreadyExistsException::class.java) {
            registerService.registerUser(registerModel)
        }

        verify(exactly = 0) { userService.saveUser(any()) }
    }

    @Test
    fun `registerUser should throw UserAlreadyExistsException when phone number is taken`() {
        every { otpService.getLatestNotExpiredOtpBySessionId(sessionId) } returns verifiedOtpLog
        every { userService.userExistsByUserName(any()) } returns false
        every { userService.userExistsByPhoneNumber(any()) } returns true

        assertThrows(UserAlreadyExistsException::class.java) {
            registerService.registerUser(registerModel)
        }

        verify(exactly = 0) { userService.saveUser(any()) }
    }

    private companion object {
        private const val DUMMY_PHONE_NUMBER = "+201176897654"
        private const val DEFAULT_REGION = "EG"
        private val dummyValidatedPhone = ValidatedPhoneNumber(
            phoneNumber = DUMMY_PHONE_NUMBER,
            regionCode = DEFAULT_REGION,
            countryCode = "20",
            carrierPrefixHeuristic = "11"
        )
        private val sessionId = UUID.randomUUID()
        private const val OTP = "00000000"
        private val verifiedOtpLog = createOtpLog(
            phoneNumber = DUMMY_PHONE_NUMBER,
            otp = OTP,
            sessionId = sessionId,
            isVerified = true
        )
        private val notVerifiedOtpLog = createOtpLog(isVerified = false)
        private const val ENCODED_PASSWORD = "encodedPassword123"
        private const val ACCESS_TOKEN = "dummyAccessToken"
        private val registerModel =
            RegisterUserModel(
                phoneNumber = DUMMY_PHONE_NUMBER,
                username = "thorayahamdy",
                firstName = "Thoraya",
                lastName = "Hamdy",
                birthDate = "2000-01-01",
                gender = 2,
                password = "12345678",
                sessionId = sessionId
            )
        private val savedUser = User(
            username = registerModel.username, phoneNumber = registerModel.phoneNumber,
            password = ENCODED_PASSWORD,
            firstName = registerModel.firstName,
            lastName = registerModel.lastName,
            imageUrl = null,
            birthDate = LocalDate.parse(registerModel.birthDate),
            gender = registerModel.gender,
            status = User.Status.ACTIVE
        )
        private val refreshToken = RefreshToken(
            refreshToken = "dummyRefreshToken",
            expiresIn = Instant.now().plus(Duration.ofDays(7)).epochSecond,
            user = savedUser
        )
    }
}