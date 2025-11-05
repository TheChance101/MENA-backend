package net.thechance.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.Locale

@Configuration
class LocalizationConfig : WebMvcConfigurer {
    @Bean
    fun localeResolver(): LocaleResolver {
        return AcceptHeaderLocaleResolver().let { localeResolver ->
            localeResolver.setDefaultLocale(defaultLocale)
            localeResolver.supportedLocales = supportedLocales
            localeResolver
        }
    }

    @Bean
    fun messageSource(): ReloadableResourceBundleMessageSource {
        return ReloadableResourceBundleMessageSource().let { messageSource ->
            messageSource.setDefaultEncoding("UTF-8")
            messageSource.setBasenames(*discoverMessageBaseNames())
            messageSource
        }
    }

    private fun discoverMessageBaseNames(): Array<String> {
        val resolver = PathMatchingResourcePatternResolver()
        val resources = resolver.getResources(RESOURCE_PATTERN)

        return resources
            .mapNotNull { it.filename }
            .map { filename ->
                val fileNameWithoutExtension = getFileNameWithoutExtension(filename)
                "classpath:messages/$fileNameWithoutExtension"
            }
            .toSet()
            .toTypedArray()
    }

    private fun getFileNameWithoutExtension(filename: String): String = filename.removeSuffix(".properties").substringBeforeLast("_")

    private companion object {
        private const val RESOURCE_PATTERN = "classpath*:messages/messages-*.properties"
        private val defaultLocale = Locale.ENGLISH
        private val supportedLocales = listOf(Locale.ENGLISH, Locale("ar"))
    }
}