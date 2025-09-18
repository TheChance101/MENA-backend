package net.thechance.trends.service

import net.thechance.trends.api.controller.exception.ReelNotFoundException
import net.thechance.trends.entity.Reel
import net.thechance.trends.repository.ReelsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReelsService(
    private val reelsRepository: ReelsRepository,
) {
    fun getAllReelsByUserId(
        pageable: Pageable,
        currentUserId: UUID
    ): Page<Reel> {

        val body = reelsRepository.findByOwnerId(
            currentUserId,
            PageRequest.of(
                maxOf(0, pageable.pageNumber - 1),
                10,
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
            )
        )
        return body
    }

    fun deleteReelById(id: UUID, currentUserId: UUID) {
        val isReelExists = reelsRepository.existsByIdAndOwnerId(id , currentUserId)

        if (!isReelExists) throw ReelNotFoundException()

        reelsRepository.deleteById(id)
    }
}