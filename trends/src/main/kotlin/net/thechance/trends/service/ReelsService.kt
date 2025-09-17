package net.thechance.trends.service

import net.thechance.trends.repository.ReelsRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ReelsService(
    private val reelsRepository: ReelsRepository,
) {

    fun deleteReelById(id: UUID) {
        val reel = reelsRepository.findById(id)
            .orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reel with id $id not found"
                )
            }

        val currentUser = SecurityContextHolder.getContext().authentication.principal as UUID
        if (reel.ownerId != currentUser)
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "You can only delete your own reels"
            )

        reelsRepository.deleteById(id)
    }
}