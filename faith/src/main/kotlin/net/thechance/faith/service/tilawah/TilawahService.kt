package net.thechance.faith.service.tilawah

import net.thechance.faith.entity.Reciter
import net.thechance.faith.exception.ReciterNotFoundException
import net.thechance.faith.repository.RecitersRepository
import net.thechance.faith.utils.getSurahAyatNumber
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class TilawahService(
    private val recitersRepository: RecitersRepository,
    private val reciterUrlGenerator: ReciterUrlGenerator
) {
    fun getReciters(): List<Reciter> = recitersRepository.findAll()

    fun getAyahSoundUrl(surahNumber: Int, ayahNumber: Int, reciterId: Int): String {
        val reciter = recitersRepository.findById(reciterId).getOrElse {
            throw ReciterNotFoundException(message = "Reciter with id $reciterId not found")
        }
        return reciterUrlGenerator.generateAyahSoundUrl(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            reciter = reciter
        )
    }

    fun getSurahAyatSoundUrl(surahNumber: Int, reciterId: Int): List<String> {
        val reciter = recitersRepository.findById(reciterId).getOrElse {
            throw ReciterNotFoundException(message = "Reciter with id $reciterId not found")
        }
        val surahAyatNumber = getSurahAyatNumber(surahNumber)

        return reciterUrlGenerator.generateAyatSoundUrl(
            surahNumber = surahNumber,
            ayatNumber = surahAyatNumber,
            reciter = reciter
        )
    }

    fun getSurahSoundsUrl(surahNumber: Int, reciterId: Int): String {
        val reciter = recitersRepository.findById(reciterId).getOrElse {
            throw ReciterNotFoundException(message = "Reciter with id $reciterId not found")
        }
        return reciterUrlGenerator.generateSurahSoundsUrl(
            surahNumber = surahNumber,
            reciter = reciter
        )
    }
}
