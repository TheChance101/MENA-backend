package net.thechance.identity.security.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage.mena")
data class IdentityStorageProperties(
    val bucket: String,
    val cdnEndpoint: String
)