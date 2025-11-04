package net.thechance.faith.service

import net.thechance.faith.entity.Mosque
import net.thechance.faith.exception.InvalidRequestParameterException
import net.thechance.faith.repository.MosqueRepository
import org.springframework.stereotype.Service

@Service
class MosqueService(private val mosqueRepository: MosqueRepository) {

    fun findNearby(lat: Double, lng: Double, radiusKm: Double): List<Mosque> {
        if (radiusKm <= 0) throw InvalidRequestParameterException("Parameter 'radiusKm' must be greater than 0.")

       return mosqueRepository.findNearbyMosques(lat, lng, radiusKm)
    }
}
