package net.thechance.identity.service

import net.thechance.identity.exception.FrequentOtpRequestException
import net.thechance.identity.repository.OtpLogRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PhoneNumberRateLimitService(
    private val otpLogRepository: OtpLogRepository
) {
    fun checkRequestLimit(phoneNumber: String) {
        val pageable = PageRequest.of(0, MAX_RETRIES_PER_WINDOW)
        val latestOtpLog = otpLogRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber, pageable)
        if (latestOtpLog.isNotEmpty()) {
            val isLastRequestIsFrequent =
                latestOtpLog.first().createdAt > Instant.now().minusSeconds(MIN_OTP_RESEND_GAP_SECONDS)
            val ifWindowLimitExceeded =
                latestOtpLog.size == MAX_RETRIES_PER_WINDOW && latestOtpLog.last().createdAt > Instant.now()
                    .minusSeconds(
                        WINDOW_DURATION_IN_SECONDS
                    )
            if (isLastRequestIsFrequent || ifWindowLimitExceeded) throw FrequentOtpRequestException()
        }
    }

    companion object {
        private const val MIN_OTP_RESEND_GAP_SECONDS = 60L
        private const val MAX_RETRIES_PER_WINDOW = 5
        private const val WINDOW_DURATION_IN_SECONDS = 10 * 60L
    }
}