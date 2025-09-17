package net.thechance.trends.service

import net.thechance.trends.repository.ReelsRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReelsService(
    private val reelsRepository: ReelsRepository,
) {

    fun deleteReelById(id: UUID, currentUserId: UUID) {
        val isReelExists = reelsRepository.existsByIdAndOwnerId(id , currentUserId)

        if (!isReelExists) throw NoSuchElementException("This reel cannot be found")

        reelsRepository.deleteById(id)
    }
}