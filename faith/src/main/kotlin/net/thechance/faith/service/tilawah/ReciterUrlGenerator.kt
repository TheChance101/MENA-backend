package net.thechance.faith.service.tilawah

import net.thechance.faith.entity.Reciter
import net.thechance.faith.utils.padWithThreeDigits
import org.springframework.stereotype.Component

@Component
class ReciterUrlGenerator {

    fun generateAyahSoundUrl(surahNumber: Int, ayahNumber: Int, reciter: Reciter): String {
        return "${reciter.serverUrl}${surahNumber.padWithThreeDigits()}${ayahNumber.padWithThreeDigits()}.mp3"
    }

    fun generateSurahSoundsUrl(surahNumber: Int, reciter: Reciter): String {
        return "${reciter.serverUrl}zips/${surahNumber.padWithThreeDigits()}.zip"
    }
}
