package net.thechance.trends.service

import net.thechance.trends.entity.Reel
import net.thechance.trends.entity.ReelLike
import net.thechance.trends.entity.ReelView
import net.thechance.trends.exception.ReelNotFoundException
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.exception.VideoDeleteFailedException
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.ReelLikeRepository
import net.thechance.trends.repository.ReelViewRepository
import net.thechance.trends.repository.ReelsRepository
import net.thechance.trends.models.ReelWithLikeStatus
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
    private val fileStorageService: FileStorageService,
    private val reelViewRepository: ReelViewRepository,
    private val reelLikeRepository: ReelLikeRepository
) {
    fun getAllReelsByUserId(
        pageable: Pageable,
        currentUserId: UUID
    ): Page<ReelWithLikeStatus> {

        val body = reelsRepository.findByOwnerIdAndIsPublished(
            currentUserId,
            true,
            PageRequest.of(
                pageable.pageNumber,
                10,
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
            ),
        )
        return body
    }

    fun getAllReelsForFeed(
        pageable: Pageable,
        currentUserId: UUID,
        reelId: UUID? = null,
    ): Page<ReelWithLikeStatus> {
        val adjustedPageable = PageRequest.of(
            pageable.pageNumber,
            10,
            pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
        )

        return reelsRepository.getReelFeedForUser(currentUserId, reelId, adjustedPageable)
    }

    @Transactional
    fun deleteReelById(id: UUID, currentUserId: UUID) {
        val reelVideoUrl = reelsRepository.findVideoUrlByIdAndOwnerId(id, currentUserId)
            ?: throw ReelNotFoundException()

        runCatching {
            if (reelsRepository.deleteReelById(id) != 0) fileStorageService.deleteVideo(reelVideoUrl)
        }.onFailure {
            throw VideoDeleteFailedException()
        }
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

        val categories = categoryIds.map { categoryRepository.getReferenceById(it) }.toMutableSet()
        if (categories.isEmpty()) throw TrendCategoryNotFoundException()

        val updatedReel = existingReel.getReel().copy(
            description = newDescription,
            categories = categories,
            isPublished = true
        )

        return reelsRepository.save(updatedReel)
    }


    fun uploadReel(currentUserId: UUID, file: MultipartFile): UUID {
        val videoUrl = fileStorageService.uploadVideo(file = file)
        val reel = Reel(
            ownerId = currentUserId,
            videoUrl = videoUrl
        )
        return reelsRepository.save(reel).id
    }

    @Transactional
    fun uploadThumbnail(
        reelId: UUID,
        ownerId: UUID,
        thumbnailFile: MultipartFile
    ): Reel {
        val existingReel = reelsRepository.findByIdAndOwnerId(id = reelId, ownerId = ownerId)
            ?: throw ReelNotFoundException()

        val thumbnailUrl = fileStorageService.uploadImage(file = thumbnailFile)

        val updatedReel = existingReel.getReel().copy(thumbnailUrl = thumbnailUrl)

        return reelsRepository.save(updatedReel)
    }

    @Transactional
    fun incrementViewCount(reelId: UUID, userId: UUID) {
        reelViewRepository.save(ReelView(reelId = reelId, userId = userId))
    }

    fun likeReel(reelId: UUID, userId: UUID): ReelWithLikeStatus {
        reelLikeRepository.save(ReelLike(reelId = reelId, userId = userId))
        return getReelOrThrow(reelId, userId)
    }

    @Transactional
    fun unlikeReel(reelId: UUID, userId: UUID): ReelWithLikeStatus {
        reelLikeRepository.deleteReelLikeByReelIdAndUserId(reelId, userId)
        return getReelOrThrow(reelId, userId)
    }

    fun getReelOrThrow(reelId: UUID, userId: UUID): ReelWithLikeStatus {
        return reelsRepository.findByIdAndIsPublishedWithLikeStatus(reelId = reelId, isPublished = true, userId = userId) ?: throw ReelNotFoundException()
    }
}