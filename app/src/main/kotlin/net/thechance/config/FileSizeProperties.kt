package net.thechance.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "file-size")
@Component
class FileSizeProperties {
    var modules: Map<String, Int> = emptyMap()

    fun getMaxSize(module: String): Int = modules[module] ?: 5
}