package net.thechance.identity.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
@ConfigurationProperties(prefix = "identity-rate-limit")
class RateLimitProperties {
    val endpoints: ConcurrentHashMap<String, EndpointRateLimitConfig> = ConcurrentHashMap()

    data class EndpointRateLimitConfig(
        val shortTermAttemptsLimit: Int = 5,
        val longTermAttemptsLimit: Int = 5,
        val shortTermWindowSeconds: Long = 60,
        val longTermWindowSeconds: Long = 60,
        val blockDurationSeconds: Long = 900
    )
}