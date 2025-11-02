package net.thechance.faith.service

import net.thechance.faith.entity.Reciter
import net.thechance.faith.repository.reciters.RecitersRepository
import org.springframework.stereotype.Service

@Service
class RecitersService(
    private val recitersRepository: RecitersRepository
) {
    fun getReciters(): List<Reciter> = recitersRepository.findAll()
}
