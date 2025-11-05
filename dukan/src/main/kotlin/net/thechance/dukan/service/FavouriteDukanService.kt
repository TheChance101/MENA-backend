package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.FavoriteDukan
import net.thechance.dukan.repository.FavoriteDukanRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FavouriteDukanService(
    private val favoriteDukanRepository: FavoriteDukanRepository
) {
    @Transactional
    fun toggleFavoriteStatus(userId: UUID, dukanId: UUID) {
        val favorite = favoriteDukanRepository.findByUserIdAndDukanId(userId, dukanId)

        if (favorite != null) favoriteDukanRepository.delete(favorite)
        else favoriteDukanRepository.save(FavoriteDukan(userId = userId, dukanId = dukanId))
    }

    fun getUserFavorites(userId: UUID): List<FavoriteDukan> {
        return favoriteDukanRepository.findAllByUserId(userId)
    }

    fun isFavorite(userId: UUID, dukanId: UUID): Boolean {
        return favoriteDukanRepository.findByUserIdAndDukanId(userId, dukanId) != null
    }
}