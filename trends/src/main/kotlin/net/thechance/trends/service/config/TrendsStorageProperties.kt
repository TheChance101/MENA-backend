package net.thechance.trends.service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage.trends")
data class TrendsStorageProperties(
    val bucket: String,
    val cdnEndpoint: String,
    val key: String,
    val secret: String,
    val endpoint: String,
)
