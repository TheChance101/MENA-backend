package net.thechance.config.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage")
data class AllStorageProperties(
    val mena: StorageProperties,
    val dukan: StorageProperties,
    val trends: StorageProperties,
    val wallet: StorageProperties
)

data class StorageProperties(
    val key: String,
    val secret: String,
    val endpoint: String,
    val bucket: String,
    val cdnEndpoint: String
)