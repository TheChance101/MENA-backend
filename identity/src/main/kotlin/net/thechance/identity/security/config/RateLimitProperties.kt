package net.thechance.identity.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
@ConfigurationProperties(prefix = "identity-rate-limit")
class RateLimitProperties {
    val globalMaxIpsToTrack: Long = 100_000
    val endpoints: ConcurrentHashMap<String, EndpointRateLimitConfig> = ConcurrentHashMap()

    data class EndpointRateLimitConfig(
        val shortTermLimit: Long = 5,
        val shortTermWindowSeconds: Long = 60,

        val longTermLimit: Long = 5,
        val longTermWindowSeconds: Long = 60,

        val blockDurationSeconds: Long = 900
    )
}