package net.thechance.identity.service

import io.mockk.*
import net.thechance.identity.exception.InvalidOtpException
import net.thechance.identity.exception.OtpExpiredException
import net.thechance.identity.repository.OtpLogRepository
import net.thechance.identity.service.otpGenerator.OtpGeneratorService
import net.thechance.identity.utils.createOtpLog
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.util.UUID

class OtpServiceTest {
    private val otpGeneratorService: OtpGeneratorService = mockk(relaxed = true)
    private val otpLogRepository: OtpLogRepository = mockk(relaxed = true)
    private val otpService: OtpService = OtpService(otpGeneratorService, otpLogRepository)

    @Test
    fun `createOtp() should expire old OTPs, generate a new one, save it, and return it`() {
        every { otpGeneratorService.generateOtp() } returns OTP
        every { otpLogRepository.updateExpirationByPhoneNumber(any(), any()) } just runs
        every { otpLogRepository.save(any()) } returns mockk()

        otpService.createOtp(PHONE_NUMBER)

        verify(exactly = 1) { otpLogRepository.updateExpirationByPhoneNumber(any(), any()) }
        verify(exactly = 1) { otpGeneratorService.generateOtp() }
        verify(exactly = 1) { otpLogRepository.save(any()) }
    }

    @Test
    fun `verifyOtp() should succeed for a valid, non-expired, and unverified OTP`() {
        every { otpLogRepository.findByOtpAndSessionId(OTP, SESSION_ID_UUID) } returns validOtpLog

        otpService.verifyOtp(OTP, SESSION_ID)

        verify(exactly = 1) { otpLogRepository.verifyOtp(SESSION_ID_UUID) }
    }

    @Test
    fun `verifyOtp() should throw InvalidOtpException when OTP is not found`() {
        every { otpLogRepository.findByOtpAndSessionId(any(), any()) } returns null

        assertThrows(InvalidOtpException::class.java) {
            otpService.verifyOtp(OTP, SESSION_ID)
        }
    }

    @Test
    fun `verifyOtp() should throw InvalidOtpException when OTP is already verified`() {
        every { otpLogRepository.findByOtpAndSessionId(OTP, SESSION_ID_UUID) } returns alreadyVerifiedLog

        assertThrows(InvalidOtpException::class.java) {
            otpService.verifyOtp(OTP, SESSION_ID)
        }
    }

    @Test
    fun `verifyOtp() should throw OtpExpiredException when OTP has expired`() {
        every { otpLogRepository.findByOtpAndSessionId(OTP, SESSION_ID_UUID) } returns expiredLog

        assertThrows(OtpExpiredException::class.java) {
            otpService.verifyOtp(OTP, SESSION_ID)
        }
    }

    @Test
    fun `verifyOtp() should throw IllegalArgumentException for invalid session ID format`() {
        assertThrows(IllegalArgumentException::class.java) {
            otpService.verifyOtp(OTP, INVALID_SESSION_ID)
        }
    }

    companion object {
        private const val PHONE_NUMBER = "+201122334455"
        private const val OTP = "000000"
        private const val INVALID_SESSION_ID = "not-a-valid-uuid"
        private val SESSION_ID_UUID = UUID.randomUUID()
        private val SESSION_ID = SESSION_ID_UUID.toString()

        private val validOtpLog = createOtpLog(expireAt = Instant.now().plusSeconds(120))
        private val alreadyVerifiedLog = createOtpLog(isVerified = true)
        private val expiredLog = createOtpLog(expireAt = Instant.now().minusSeconds(10))
    }
}