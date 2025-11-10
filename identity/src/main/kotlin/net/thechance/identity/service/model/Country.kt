package net.thechance.identity.service.model

import net.thechance.identity.service.utils.isRtl
import org.springframework.context.MessageSource
import java.util.*

enum class Country(
    val countryNameStringKey: String,
    val callingCode: String,
    val countryCodeName: String,
    val flagEmoji: String
) {

    BAHRAIN("identity.country.bahrain", "+973", "BH", "🇧🇭"),
    ALGERIA("identity.country.algeria", "+213", "DZ", "🇩🇿"),
    EGYPT("identity.country.egypt", "+20", "EG", "🇪🇬"),
    IRAN("identity.country.iran", "+98", "IR", "🇮🇷"),
    IRAQ("identity.country.iraq", "+964", "IQ", "🇮🇶"),
    JORDAN("identity.country.jordan", "+962", "JO", "🇯🇴"),
    KUWAIT("identity.country.kuwait", "+965", "KW", "🇰🇼"),
    LEBANON("identity.country.lebanon", "+961", "LB", "🇱🇧"),
    LIBYA("identity.country.libya", "+218", "LY", "🇱🇾"),
    MOROCCO("identity.country.morocco", "+212", "MA", "🇲🇦"),
    OMAN("identity.country.oman", "+968", "OM", "🇴🇲"),
    PALESTINE("identity.country.palestine", "+970", "PS", "🇵🇸"),
    QATAR("identity.country.qatar", "+974", "QA", "🇶🇦"),
    SAUDI_ARABIA("identity.country.saudi_arabia", "+966", "SA", "🇸🇦"),
    SOMALIA("identity.country.somalia", "+252", "SO", "🇸🇴"),
    SUDAN("identity.country.sudan", "+249", "SD", "🇸🇩"),
    SYRIA("identity.country.syria", "+963", "SY", "🇸🇾"),
    TUNISIA("identity.country.tunisia", "+216", "TN", "🇹🇳"),
    UAE("identity.country.uae", "+971", "AE", "🇦🇪"),
    YEMEN("identity.country.yemen", "+967", "YE", "🇾🇪");


    fun getLocalizedCountryName(messageSource: MessageSource, locale: Locale): LocalizedCountry {
        val localizedName = messageSource.getMessage(countryNameStringKey, null, locale)
        val formattedCode = if (locale.isRtl()) callingCode.drop(1) + "+" else callingCode

        return LocalizedCountry(
            name = localizedName,
            callingCode = formattedCode,
            countryCodeName = countryCodeName,
            flagEmoji = flagEmoji
        )
    }
}