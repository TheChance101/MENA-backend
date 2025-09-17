package net.thechance.trends.service

import net.thechance.trends.repository.ReelsRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReelsService(
    private val reelsRepository: ReelsRepository,
) {

    fun deleteReelById(id: UUID) {
        val reel = reelsRepository.findById(id)
            .orElseThrow { NoSuchElementException("Reel with id $id not found") }

        val currentUserId = SecurityContextHolder.getContext().authentication.principal as UUID
        if (reel.ownerId != currentUserId) {
            throw IllegalAccessException("You can only delete your own reels")
        }

        reelsRepository.deleteById(id)
    }
}