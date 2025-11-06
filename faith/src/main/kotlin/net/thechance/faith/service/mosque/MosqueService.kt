package net.thechance.faith.service.mosque

import jakarta.transaction.Transactional
import net.thechance.faith.entity.Mosque
import net.thechance.faith.exception.InvalidRequestParameterException
import net.thechance.faith.repository.MosqueRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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


    fun searchMosquesByName(keyword: String, pageable: Pageable): Page<Mosque> {
        if (keyword.isBlank()) throw InvalidRequestParameterException("Parameter 'keyword' must not be blank.")

        return mosqueRepository.searchMosquesByName(keyword, pageable)
    }

    fun findNearby(lat: Double, lng: Double, radiusKm: Double): List<Mosque> {
        if (radiusKm <= 0) throw InvalidRequestParameterException("Parameter 'radiusKm' must be greater than 0.")

        return mosqueRepository.findNearbyMosques(lat, lng, radiusKm)
    }
}

