package net.thechance.faith.service.tilawah

import net.thechance.faith.entity.Reciter

interface ReciterUrlGenerator {

    fun generateAyahSoundUrl(surahNumber: Int, ayahNumber: Int, reciter: Reciter): String
    fun generateAyatSoundUrl(surahNumber: Int, ayatNumber: Int, reciter: Reciter): List<String>
    fun generateSurahSoundsUrl(surahNumber: Int, reciter: Reciter): String
}
