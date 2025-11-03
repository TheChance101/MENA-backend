package net.thechance.faith.service.mosque

import jakarta.transaction.Transactional
import net.thechance.faith.entity.Mosque
import net.thechance.faith.repository.MosqueRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class MosqueService(
    private val mosqueRepository: MosqueRepository,
) {

    fun createNearestMosque(mosque: Mosque): Mosque {
        return mosqueRepository.save(mosque)
    }

    @Transactional
    fun updateMosqueImage(id: UUID, imageUrl: String): Mosque {
        val mosque = mosqueRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Mosque not found") }

        val updatedMosque = mosque.copy(imageUrl = imageUrl)
        return mosqueRepository.save(updatedMosque)
    }

    fun getMosqueById(mosqueId: UUID): Mosque? {
        return mosqueRepository.findById(mosqueId).orElse(null)
    }
}

