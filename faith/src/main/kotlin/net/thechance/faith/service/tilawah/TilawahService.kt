package net.thechance.faith.service.tilawah

import net.thechance.faith.entity.Reciter
import net.thechance.faith.exception.ReciterNotFoundException
import net.thechance.faith.repository.RecitersRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class TilawahService(
    private val recitersRepository: RecitersRepository,
    private val ayahSoundUrlGenerator: AyahSoundUrlGenerator
) {
    fun getReciters(): List<Reciter> = recitersRepository.findAll()

    fun getAyahSoundUrl(surahNumber: Int, ayahNumber: Int, reciterId: Int): String {
        val reciter = recitersRepository.findById(reciterId).getOrElse {
            throw ReciterNotFoundException("Reciter with id $reciterId not found")
        }
        return ayahSoundUrlGenerator.generateUrl(surahNumber, ayahNumber, reciter)
    }
}
