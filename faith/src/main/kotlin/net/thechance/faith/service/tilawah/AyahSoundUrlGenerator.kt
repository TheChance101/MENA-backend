package net.thechance.faith.service.tilawah

import net.thechance.faith.entity.Reciter
import org.springframework.stereotype.Component

@Component
class AyahSoundUrlGenerator {

    fun generateUrl(surahNumber: Int, ayahNumber: Int, reciter: Reciter): String {
        val reciterUrl = reciter.serverUrl
        return "$reciterUrl${padWithThreeDigits(surahNumber)}${padWithThreeDigits(ayahNumber)}.mp3"
    }

    private fun padWithThreeDigits(number: Int): String {
        return number.toString().padStart(3, '0')
    }
}
