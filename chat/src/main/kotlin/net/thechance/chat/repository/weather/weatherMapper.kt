package net.thechance.chat.repository.weather

import net.thechance.chat.service.model.WeatherModel
import net.thechance.chat.service.model.WeatherType

fun WeatherDTO.toEntity(): WeatherModel = WeatherModel(
    currentTemperature = current?.temperature ?: 0.0,
    minTemperature = daily?.minTemperature?.firstOrNull() ?: 0.0,
    maxTemperature = daily?.maxTemperature?.firstOrNull() ?: 0.0,
    weatherType = current?.weatherCode.toWeatherType()
)

private fun Int?.toWeatherType(): WeatherType = when (this) {
    0 -> WeatherType.CLEAR_SKY
    1 -> WeatherType.MAINLY_CLEAR
    2 -> WeatherType.PARTLY_CLOUDY
    3 -> WeatherType.OVERCAST
    45 -> WeatherType.FOG
    48 -> WeatherType.DEPOSITING_RIME_FOG
    51 -> WeatherType.LIGHT_DRIZZLE
    53 -> WeatherType.MODERATE_DRIZZLE
    55 -> WeatherType.DENSE_DRIZZLE
    56 -> WeatherType.LIGHT_FREEZING_DRIZZLE
    57 -> WeatherType.DENSE_FREEZING_DRIZZLE
    61 -> WeatherType.SLIGHT_RAIN
    63 -> WeatherType.MODERATE_RAIN
    65 -> WeatherType.HEAVY_RAIN
    66 -> WeatherType.LIGHT_FREEZING_RAIN
    67 -> WeatherType.HEAVY_FREEZING_RAIN
    71 -> WeatherType.SLIGHT_SNOWFALL
    73 -> WeatherType.MODERATE_SNOWFALL
    75 -> WeatherType.HEAVY_SNOWFALL
    77 -> WeatherType.SNOW_GRAINS
    80 -> WeatherType.SLIGHT_RAIN_SHOWER
    81 -> WeatherType.MODERATE_RAIN_SHOWER
    82 -> WeatherType.VIOLENT_RAIN_SHOWER
    85 -> WeatherType.SLIGHT_SNOW_SHOWER
    86 -> WeatherType.HEAVY_SNOW_SHOWER
    95 -> WeatherType.MODERATE_THUNDERSTORM
    96 -> WeatherType.SLIGHT_HAIL_THUNDERSTORM
    99 -> WeatherType.HEAVY_HAIL_THUNDERSTORM
    else -> WeatherType.UNKNOWN
}