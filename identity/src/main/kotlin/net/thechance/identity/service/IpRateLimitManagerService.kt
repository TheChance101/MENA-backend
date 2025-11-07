package net.thechance.identity.service

import net.thechance.identity.entity.RequestLog
import net.thechance.identity.security.config.RateLimitProperties
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class IpRateLimitManagerService(
    private val rateLimitProperties: RateLimitProperties,
    private val logService: LoginLogService,
    private val loginLogService: LoginLogService
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
        return isShortTermBlock(ipAddress, url, config).not() || isLongTermBlock(ipAddress, url, config).not()
    }

    private fun isShortTermBlock(
        ipAddress: String,
        url: String,
        config: RateLimitProperties.EndpointRateLimitConfig
    ): Boolean {
        return isUserBlocked(
            ipAddress = ipAddress,
            url = url,
            maxValidUserAttempts = config.shortTermAttemptsLimit,
            maxWindowTimeInSeconds = config.shortTermWindowSeconds,
            blockTimeInSeconds = config.blockDurationSeconds
        )
    }

    private fun isLongTermBlock(
        ipAddress: String,
        url: String,
        config: RateLimitProperties.EndpointRateLimitConfig
    ): Boolean {
        return isUserBlocked(
            ipAddress = ipAddress,
            url = url,
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
        loginLogService.addLoginLog(requestLog)
    }

    private fun isUserBlocked(
        ipAddress: String,
        url: String,
        maxValidUserAttempts: Int,
        maxWindowTimeInSeconds: Long,
        blockTimeInSeconds: Long
    ): Boolean {
        val loginLogs = logService.getLoginLogsByIpAddress(
            ipAddress = ipAddress,
            url = url,
            maxValidUserAttempts
        )

        return loginLogs.isNotEmpty()
                && !isUserAttemptsWithInLimit(loginLogs, maxValidUserAttempts)
                && !isCurrentTimeWithInBlockRange(loginLogs, blockTimeInSeconds)
                && isDurationBetweenFirstAndLastLogWithInWindowRange(loginLogs, maxWindowTimeInSeconds)
    }

    private fun isUserAttemptsWithInLimit(
        requestLogs: List<RequestLog>,
        maxValidUserAttempts: Int
    ) = requestLogs.size < maxValidUserAttempts

    private fun isCurrentTimeWithInBlockRange(
        requestLogs: List<RequestLog>,
        blockTimeInSeconds: Long
    ): Boolean {
        val lastTimeToLogin = requestLogs.first().loginTime
        val now = Instant.now()
        val durationSinceLastLogin = Duration.between(lastTimeToLogin, now)
        return durationSinceLastLogin.toSeconds() >= blockTimeInSeconds
    }

    private fun isDurationBetweenFirstAndLastLogWithInWindowRange(
        requestLogs: List<RequestLog>,
        maxWindowTimeInSeconds: Long
    ): Boolean {
        val lastTimeToLogin = requestLogs.first().loginTime
        val firstTimeToLogin = requestLogs.last().loginTime
        val durationBetweenFirstAndLastLogin = Duration.between(firstTimeToLogin, lastTimeToLogin)
        return durationBetweenFirstAndLastLogin.toSeconds() <= maxWindowTimeInSeconds
    }
}