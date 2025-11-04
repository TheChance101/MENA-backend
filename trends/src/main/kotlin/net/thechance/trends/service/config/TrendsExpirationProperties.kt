package net.thechance.trends.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "trends-expiration")
@PropertySource("classpath:trends-expiration.properties")
data class TrendsExpirationProperties(
    val videoUrlMinutes: Long,
    val thumbnailUrlMinutes: Long
)