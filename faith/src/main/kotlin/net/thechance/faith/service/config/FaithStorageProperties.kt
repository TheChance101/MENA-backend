package net.thechance.faith.service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage.mena")
data class FaithStorageProperties(
    val bucket: String,
    val cdnEndpoint: String
)