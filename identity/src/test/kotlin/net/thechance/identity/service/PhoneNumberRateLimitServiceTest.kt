package net.thechance.identity.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.identity.exception.FrequentOtpRequestException
import net.thechance.identity.repository.OtpLogRepository
import net.thechance.identity.utils.createOtpLog
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class PhoneNumberRateLimitServiceTest {
    private val otpLogRepository: OtpLogRepository = mockk(relaxed = true)
    private var phoneNumberRateLimitService: PhoneNumberRateLimitService = PhoneNumberRateLimitService(otpLogRepository)

    @Test
    fun `checkRequestLimit() should pass when there are no previous OTP logs`() {
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns emptyList()

        phoneNumberRateLimitService.checkRequestLimit(PHONE_NUMBER)

        verify(exactly = 1) { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) }
    }

    @Test
    fun `checkRequestLimit() should pass when last request was long ago and window limit is not reached`() {
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns oldLogs

        phoneNumberRateLimitService.checkRequestLimit(PHONE_NUMBER)

        verify(exactly = 1) { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) }
    }

    @Test
    fun `checkRequestLimit() should throw FrequentOtpRequestException when last request was too recent`() {
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns recentLogs

        assertThrows(FrequentOtpRequestException::class.java) {
            phoneNumberRateLimitService.checkRequestLimit(PHONE_NUMBER)
        }
    }

    @Test
    fun `checkRequestLimit() should throw FrequentOtpRequestException when window limit is exceeded`() {
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns frequentLogs

        assertThrows(FrequentOtpRequestException::class.java) {
            phoneNumberRateLimitService.checkRequestLimit(PHONE_NUMBER)
        }
    }

    @Test
    fun `checkRequestLimit() should pass when max retries were made but window has passed`() {
        every { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) } returns normalLogs

        phoneNumberRateLimitService.checkRequestLimit(PHONE_NUMBER)

        verify(exactly = 1) { otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(any(), any()) }
    }

    companion object {
        private const val PHONE_NUMBER = "+201122334455"
        private val oldLogs = listOf(createOtpLog(createdAt = Instant.now().minusSeconds(70)))
        private val recentLogs = listOf(createOtpLog(createdAt = Instant.now().minusSeconds(30)))

        private val frequentLogs = listOf(
            createOtpLog(createdAt = Instant.now().minusSeconds(70)),
            createOtpLog(createdAt = Instant.now().minusSeconds(120)),
            createOtpLog(createdAt = Instant.now().minusSeconds(180)),
            createOtpLog(createdAt = Instant.now().minusSeconds(240)),
            createOtpLog(createdAt = Instant.now().minusSeconds(300))
        )

        private val normalLogs = listOf(
            createOtpLog(createdAt = Instant.now().minusSeconds(90)),
            createOtpLog(createdAt = Instant.now().minusSeconds(120)),
            createOtpLog(createdAt = Instant.now().minusSeconds(180)),
            createOtpLog(createdAt = Instant.now().minusSeconds(240)),
            createOtpLog(createdAt = Instant.now().minusSeconds(11 * 60))
        )
    }
}