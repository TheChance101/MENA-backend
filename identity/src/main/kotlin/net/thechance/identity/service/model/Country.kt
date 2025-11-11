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

    ALGERIA("identity.country.algeria", "+213", "DZ", "🇩🇿", "^(0?)([567])\\d{8}\$"),
    BAHRAIN("identity.country.bahrain", "+973", "BH", "🇧🇭", "^([36])\\d{7}\$"),
    EGYPT("identity.country.egypt", "+20", "EG", "🇪🇬", "^(0)?1[0125]\\d{8}\$"),
    IRAN("identity.country.iran", "+98", "IR", "🇮🇷", "^(0)?9\\d{9}"),
    IRAQ("identity.country.iraq", "+964", "IQ", "🇮🇶", "^(0)?7[0-9]\\d{8}\$"),
    JORDAN("identity.country.jordan", "+962", "JO", "🇯🇴", "^(0)?7[789]\\d{8}\$"),
    KUWAIT("identity.country.kuwait", "+965", "KW", "🇰🇼", "^([569]\\d{7}|41\\d{6})\$"),
    LEBANON("identity.country.lebanon", "+961", "LB", "🇱🇧", "^((3|81)\\d{6}|7\\d{7})\$"),
    LIBYA("identity.country.libya", "+218", "LY", "🇱🇾", "^(0)?(9[1-6]\\d{7}|[1-8]\\d{7,9})\$"),
    MOROCCO("identity.country.morocco", "+212", "MA", "🇲🇦", "^(0)?[5-7]\\d{8}\$"),
    OMAN("identity.country.oman", "+968", "OM", "🇴🇲", "^(9[1-9])\\d{6}\$"),
    PALESTINE("identity.country.palestine", "+970", "PS", "🇵🇸", "^(0)?5[6|9]\\d{7}"),
    QATAR("identity.country.qatar", "+974", "QA", "🇶🇦", "^[3567]\\d{7}\$"),
    SAUDI_ARABIA("identity.country.saudi_arabia", "+966", "SA", "🇸🇦", "^(0)?5\\d{8}\$"),
    SOMALIA("identity.country.somalia", "+252", "SO", "🇸🇴", "^(0)?((6[0-9])\\d{7}|(7[1-9])\\d{7})\$"),
    SUDAN("identity.country.sudan", "+249", "SD", "🇸🇩", "^(0)?(9[1257])\\d{7}\$"),
    SYRIA("identity.country.syria", "+963", "SY", "🇸🇾", "^(0)?9\\d{8}\$"),
    TUNISIA("identity.country.tunisia", "+216", "TN", "🇹🇳", "^[2459]\\d{7}\$"),
    UAE("identity.country.uae", "+971", "AE", "🇦🇪", "^(0)?5[024568]\\d{7}\$"),
    YEMEN("identity.country.yemen", "+967", "YE", "🇾🇪", "^((7|0?7)[0137]\\d{7}|((\\+|00)967|0)[1-7]\\d{6})\$");


    fun getLocalizedCountry(messageSource: MessageSource, locale: Locale): LocalizedCountry {
        val localizedName = messageSource.getMessage(countryNameStringKey, null, locale)
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