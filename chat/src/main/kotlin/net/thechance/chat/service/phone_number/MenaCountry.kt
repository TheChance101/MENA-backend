package net.thechance.chat.service.phone_number

enum class MenaCountry(
    val callingCode: String,
    val countryCodeName: String
) {
    ALGERIA("+213", "DZ"),
    BAHRAIN("+973", "BH"),
    EGYPT("+20", "EG"),
    IRAN("+98", "IR"),
    IRAQ("+964", "IQ"),
    JORDAN("+962", "JO"),
    KUWAIT("+965", "KW"),
    LEBANON("+961", "LB"),
    LIBYA("+218", "LY"),
    MOROCCO("+212", "MA"),
    OMAN("+968", "OM"),
    PALESTINE("+970", "PS"),
    QATAR("+974", "QA"),
    SAUDI_ARABIA("+966", "SA"),
    SOMALIA("+252", "SO"),
    SUDAN("+249", "SD"),
    SYRIA("+963", "SY"),
    TUNISIA("+216", "TN"),
    UAE("+971", "AE"),
    YEMEN("+967", "YE");

    companion object {
        fun fromPhoneNumber(phoneNumber: String): MenaCountry? {
            return MenaCountry.entries.find { phoneNumber.startsWith(it.callingCode) }
        }
    }
}