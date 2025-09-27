package net.thechance.trends.service

import net.thechance.trends.api.controller.exception.ReelNotFoundException
import net.thechance.trends.api.controller.exception.TrendCategoryNotFoundException
import net.thechance.trends.entity.Reel
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.ReelsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ReelsService(
    private val reelsRepository: ReelsRepository,
    private val categoryRepository: CategoryRepository,
    private val videoStorageService: VideoStorageService,
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

    @Transactional
    fun updateReelDescriptionAndCategories(
        reelId: UUID,
        ownerId: UUID,
        newDescription: String,
        categoryIds: Set<UUID>
    ): Reel {
        val existingReel = reelsRepository.findByIdAndOwnerId(id = reelId, ownerId = ownerId)
            ?: throw ReelNotFoundException()

        val categories = categoryIds.map { categoryRepository.getReferenceById(it) }.toSet()
        if (categories.isEmpty()) throw TrendCategoryNotFoundException()

        val updatedReel = existingReel.copy(
            description = newDescription,
            categories = categories
        )

        return reelsRepository.save(updatedReel)
    }


    fun uploadReel(currentUserId: UUID, file: MultipartFile): UUID {
        val videoUrl = videoStorageService.uploadVideo(
            file = file,
            fileName = file.originalFilename ?: "Untitled",
            folderName = TRENDS_FOLDER_NAME
        )
        val reel = Reel(
            ownerId = currentUserId,
            thumbnailUrl = "",
            videoUrl = videoUrl,
        )
        reelsRepository.save(reel)
        return reel.id
    }

    companion object {
        private const val TRENDS_FOLDER_NAME = "trends"
    }
}