package net.thechance.identity.service

import net.thechance.identity.entity.RequestLog
import net.thechance.identity.security.config.RateLimitProperties
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class IpRateLimitManagerService(
    private val rateLimitProperties: RateLimitProperties,
    private val requestLogService: RequestLogService
) {
    fun isRequestAllowed(ipAddress: String, requestPath: String): Boolean {
        val config = rateLimitProperties.endpoints[requestPath] ?: return true
        return isRequestAllowed(
            ipAddress = ipAddress,
            url = requestPath,
            config = config
        ).also { isRequestAllowed ->
            if (isRequestAllowed) addUserAttemptToLogs(ipAddress, requestPath)
        }
    }

    private fun isRequestAllowed(
        ipAddress: String,
        url: String,
        config: RateLimitProperties.EndpointRateLimitConfig
    ): Boolean {
        val longTermRequestLogs = requestLogService.getRequestLogsByIpAddress(
            ipAddress = ipAddress,
            url = url,
            numberOfLogs = config.longTermAttemptsLimit
        )
        val shortTermRequestLogs = longTermRequestLogs.take(config.shortTermAttemptsLimit)
        return isShortTermBlock(shortTermRequestLogs, config).not()
                || isLongTermBlock(longTermRequestLogs, config).not()
    }

    private fun isShortTermBlock(
        requestLog: List<RequestLog>,
        config: RateLimitProperties.EndpointRateLimitConfig
    ): Boolean {
        return isUserBlocked(
            requestLog = requestLog,
            maxValidUserAttempts = config.shortTermAttemptsLimit,
            maxWindowTimeInSeconds = config.shortTermWindowSeconds,
            blockTimeInSeconds = config.blockDurationSeconds
        )
    }

    private fun isLongTermBlock(
        requestLog: List<RequestLog>,
        config: RateLimitProperties.EndpointRateLimitConfig
    ): Boolean {
        return isUserBlocked(
            requestLog = requestLog,
            maxValidUserAttempts = config.longTermAttemptsLimit,
            maxWindowTimeInSeconds = config.longTermWindowSeconds,
            blockTimeInSeconds = config.blockDurationSeconds
        )
    }

    private fun addUserAttemptToLogs(
        ipAddress: String,
        url: String
    ) {
        val requestLog = RequestLog(ipAddress = ipAddress, url = url)
        requestLogService.addRequestLog(requestLog)
    }

    private fun isUserBlocked(
        requestLog: List<RequestLog>,
        maxValidUserAttempts: Int,
        maxWindowTimeInSeconds: Long,
        blockTimeInSeconds: Long
    ): Boolean {
        return requestLog.isNotEmpty()
                && !isUserAttemptsWithInLimit(requestLog, maxValidUserAttempts)
                && !isCurrentTimeWithInBlockRange(requestLog, blockTimeInSeconds)
                && isDurationBetweenFirstAndLastLogWithInWindowRange(requestLog, maxWindowTimeInSeconds)
    }

    private fun isUserAttemptsWithInLimit(
        requestLogs: List<RequestLog>,
        maxValidUserAttempts: Int
    ) = requestLogs.size < maxValidUserAttempts

    private fun isCurrentTimeWithInBlockRange(
        requestLogs: List<RequestLog>,
        blockTimeInSeconds: Long
    ): Boolean {
        val lastTimeToRequest = requestLogs.first().requestTime
        val now = Instant.now()
        val durationSinceLastRequest = Duration.between(lastTimeToRequest, now)
        return durationSinceLastRequest.toSeconds() >= blockTimeInSeconds
    }

    private fun isDurationBetweenFirstAndLastLogWithInWindowRange(
        requestLogs: List<RequestLog>,
        maxWindowTimeInSeconds: Long
    ): Boolean {
        val lastTimeToRequest = requestLogs.first().requestTime
        val firstTimeToRequest = requestLogs.last().requestTime
        val durationBetweenFirstAndLastRequest = Duration.between(firstTimeToRequest, lastTimeToRequest)
        return durationBetweenFirstAndLastRequest.toSeconds() <= maxWindowTimeInSeconds
    }
}