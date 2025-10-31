package net.thechance.identity.service

import net.thechance.identity.api.dto.AuthResponse
import net.thechance.identity.api.dto.RequestOtpResponse
import net.thechance.identity.entity.User
import net.thechance.identity.exception.UserAlreadyExistsException
import net.thechance.identity.security.JwtService
import net.thechance.identity.service.model.RegisterUserModel
import net.thechance.identity.service.phoneNumberValidator.PhoneNumberValidatorService
import net.thechance.identity.service.sms.SmsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class RegisterService(
    private val phoneNumberValidatorService: PhoneNumberValidatorService,
    private val otpService: OtpService,
    private val smsService: SmsService,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
) {

    fun requestOtp(phoneNumber: String, defaultRegion: String): RequestOtpResponse {
        val validatedPhoneNumber = phoneNumberValidatorService.validateAndParse(phoneNumber, defaultRegion)
        if (userService.userExistsByPhoneNumber(phoneNumber)) throw UserAlreadyExistsException()
        val otpLog = otpService.createOtp(validatedPhoneNumber.phoneNumber)
        smsService.sendSms(
            validatedPhoneNumber.countryCode,
            validatedPhoneNumber.carrierPrefixHeuristic,
            validatedPhoneNumber.phoneNumber,
            otpLog.otp,
        )
        return RequestOtpResponse(otpLog.sessionId.toString())
    }

    fun verifyOtp(otp: String, sessionId: UUID) {
        otpService.verifyOtp(otp, sessionId)
    }

    fun registerUser(registerUserModel: RegisterUserModel): AuthResponse {
        throwIfUserExists(registerUserModel.username, registerUserModel.phoneNumber)
        val user = saveUser(registerUserModel)
        return generateAuthResponse(user)
    }

    private fun saveUser(registerUserModel: RegisterUserModel): User {
        val user = User(
            phoneNumber = registerUserModel.phoneNumber,
            password = passwordEncoder.encode(registerUserModel.password),
            firstName = registerUserModel.firstName,
            lastName = registerUserModel.lastName,
            username = registerUserModel.username,
            imageUrl = null,
            birthDate = LocalDate.parse(registerUserModel.birthDate),
            gender = registerUserModel.gender
        )
        return userService.saveUser(user)
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        val accessToken = jwtService.generateToken(user)
        val refreshToken = refreshTokenService.createRefreshToken(user).refreshToken
        return AuthResponse(accessToken, refreshToken)
    }

    private fun throwIfUserExists(username: String, phoneNumber: String) {
        if (userService.userExistsByUserName(username)) throw UserAlreadyExistsException()
        if (userService.userExistsByPhoneNumber(phoneNumber)) throw UserAlreadyExistsException()
    }
}