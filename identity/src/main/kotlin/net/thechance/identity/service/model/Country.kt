package net.thechance.identity.service.model

import net.thechance.identity.service.utils.isRtl
import org.springframework.context.MessageSource
import java.util.*

enum class Country(
    val countryNameStringKey: String,
    val callingCode: String,
    val countryCodeName: String,
    val flagEmoji: String,
    val phoneNumberRegex: String
) {

    ALGERIA("algeria", "+213", "DZ", "🇩🇿", "^(0?)([567])\\d{8}$"),
    BAHRAIN("bahrain", "+973", "BH", "🇧🇭", "^([36])\\d{7}$"),
    EGYPT("egypt", "+20", "EG", "🇪🇬", "^(0)?1[0125]\\d{8}$"),
    IRAN("iran", "+98", "IR", "🇮🇷", "^(0)?9\\d{9}$"),
    IRAQ("iraq", "+964", "IQ", "🇮🇶", "^(0)?7[0-9]\\d{8}$"),
    JORDAN("jordan", "+962", "JO", "🇯🇴", "^(0)?7[789]\\d{7}$"),
    KUWAIT("kuwait", "+965", "KW", "🇰🇼", "^([569]\\d{7}|41\\d{6})$"),
    LEBANON("lebanon", "+961", "LB", "🇱🇧", "^((3|81)\\d{6}|7\\d{7})$"),
    LIBYA("libya", "+218", "LY", "🇱🇾", "^(0)?(9[1-6]\\d{7}|[1-8]\\d{7,9})$"),
    MOROCCO("morocco", "+212", "MA", "🇲🇦", "^(0)?[5-7]\\d{8}$"),
    OMAN("oman", "+968", "OM", "🇴🇲", "^(9[1-9])\\d{6}$"),
    PALESTINE("palestine", "+970", "PS", "🇵🇸", "^(0)?5[69]\\d{7}$"),
    QATAR("qatar", "+974", "QA", "🇶🇦", "^[3567]\\d{7}$"),
    SAUDI_ARABIA("saudi_arabia", "+966", "SA", "🇸🇦", "^(0)?5\\d{8}$"),
    SOMALIA("somalia", "+252", "SO", "🇸🇴", "^(0)?((6[0-9])\\d{7}|(7[1-9])\\d{7})$"),
    SUDAN("sudan", "+249", "SD", "🇸🇩", "^(0)?(9[0125679])\\d{7}$"),
    SYRIA("syria", "+963", "SY", "🇸🇾", "^(0)?9\\d{8}$"),
    TUNISIA("tunisia", "+216", "TN", "🇹🇳", "^[2459]\\d{7}$"),
    UAE("uae", "+971", "AE", "🇦🇪", "^(0)?5[024568]\\d{7}$"),
    YEMEN("yemen", "+967", "YE", "🇾🇪", "^((7|0?7)[0137]\\d{7}|((\\+|00)967|0)[1-7]\\d{6})$");


    fun getLocalizedCountry(messageSource: MessageSource, locale: Locale): LocalizedCountry {
        val localizedName = messageSource.getMessage("identity.country.$countryNameStringKey", null, locale)
        val formattedCode = if (locale.isRtl()) callingCode.drop(1) + "+" else callingCode

        return LocalizedCountry(
            name = localizedName,
            callingCode = formattedCode,
            countryCodeName = countryCodeName,
            flagEmoji = flagEmoji,
            phoneNumberRegex = phoneNumberRegex
        )
    }
}