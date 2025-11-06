package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.DukanWithFavorite
import net.thechance.dukan.entity.FavoriteDukan
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.FavoriteDukanRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class FavouriteDukanService(
    private val favoriteDukanRepository: FavoriteDukanRepository,
    private val dukanRepository: DukanRepository
) {

    @Transactional
    fun toggleFavoriteStatus(userId: UUID, dukanId: UUID): Boolean {
        print("sdffdkjslf")
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

    fun getAllByCategory(categoryId: UUID, userId: UUID, pageable: Pageable): Page<DukanWithFavorite> =
        dukanRepository.findAllByCategoryWithFavorite(categoryId, userId, pageable)

//    fun getEditorPicks(userId: UUID, pageable: Pageable): Page<Dukan> =
//        dukanRepository.findEditorPicksWithFavorite(userId, pageable)
}