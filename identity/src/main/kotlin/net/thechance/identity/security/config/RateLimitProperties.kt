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
        var shortTermLimit: Long = 5,
        var shortTermWindowSeconds: Long = 60,

        var longTermLimit: Long = 5,
        var longTermWindowSeconds: Long = 60,

        var blockDurationSeconds: Long = 900
    )
}