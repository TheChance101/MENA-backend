package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.FavoriteDukan
import net.thechance.dukan.repository.FavoriteDukanRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FavouriteDukanService(
    private val favoriteDukanRepository: FavoriteDukanRepository,
) {

    @Transactional
    fun toggleFavoriteStatus(userId: UUID, dukanId: UUID): Boolean {
        val fav = favoriteDukanRepository.findByUserIdAndDukanId(userId, dukanId)
        return if (fav != null) {
            favoriteDukanRepository.delete(fav)
            false
        } else {

            favoriteDukanRepository.save(
                FavoriteDukan(
                    userId = userId,
                    dukanId = dukanId,
                )
            )
            true
        }
    }

    fun isFavorite(userId: UUID, dukanId: UUID): Boolean =
        favoriteDukanRepository.findByUserIdAndDukanId(userId, dukanId) != null


}