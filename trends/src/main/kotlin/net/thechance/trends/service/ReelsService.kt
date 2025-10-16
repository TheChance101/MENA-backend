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
    ): Page<Reel> {

        val body = reelsRepository.findByOwnerIdAndIsPublished(
            currentUserId,
            true,
            PageRequest.of(
                maxOf(0, pageable.pageNumber - 1),
                10,
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
            )
        )
        return body
    }

    fun getAllReelsForFeed(
        pageable: Pageable,
        currentUserId: UUID,
        reelId: UUID? = null,
    ): Page<Reel> {
        val adjustedPageable = PageRequest.of(
            maxOf(0, pageable.pageNumber - 1),
            10,
            pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
        )

        return reelsRepository.getReelFeedForUser(currentUserId, reelId, adjustedPageable)
    }

    @Transactional
    fun deleteReelById(id: UUID, currentUserId: UUID) {
        val reel = reelsRepository.findByIdAndOwnerId(id, currentUserId)
            ?: throw ReelNotFoundException()

        runCatching {
            if (fileStorageService.deleteVideo(reel.videoUrl)) reelsRepository.deleteById(id)
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

        val updatedReel = existingReel.copy(
            description = newDescription,
            categories = categories,
            isPublished = true
        )

        return reelsRepository.save(updatedReel)
    }


    fun uploadReel(currentUserId: UUID, file: MultipartFile): UUID {
        val videoUrl = fileStorageService.uploadVideo(
            file = file,
            fileName = file.originalFilename ?: "Untitled",
            folderName = TRENDS_FOLDER_NAME
        )
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

        val thumbnailUrl = fileStorageService.uploadImage(
            file = thumbnailFile,
            fileName = thumbnailFile.originalFilename ?: "thumbnail",
            folderName = TRENDS_FOLDER_NAME
        )

        val updatedReel = existingReel.copy(thumbnailUrl = thumbnailUrl)

        return reelsRepository.save(updatedReel)
    }

    @Transactional
    fun incrementViewCount(reelId: UUID, userId: UUID) {
        if (!reelViewRepository.existsByReelIdAndUserId(reelId, userId)) {
            reelViewRepository.save(ReelView(reelId = reelId, userId = userId))

            reelsRepository.findById(reelId).ifPresent { reel ->
                reelsRepository.save(reel.copy(viewsCount = reel.viewsCount + 1))
            }
        }
    }

    @Transactional
    fun toggleLike(reelId: UUID, currentUserId: UUID) {
        val reel = reelsRepository.findById(reelId)
            .orElseThrow { ReelNotFoundException() }

        val isCurrentlyLiked = reelLikeRepository.existsByReelIdAndUserId(reelId, currentUserId)

        if (isCurrentlyLiked) {
            unlikeReel(reelId, currentUserId, reel)
        } else {
            likeReel(reelId, currentUserId, reel)
        }
    }

    private fun likeReel(reelId: UUID, userId: UUID, reel: Reel) {
        reelLikeRepository.save(ReelLike(reelId = reelId, userId = userId))
        reelsRepository.save(reel.copy(likesCount = reel.likesCount + 1))
    }

    private fun unlikeReel(reelId: UUID, userId: UUID, reel: Reel) {
        reelLikeRepository.deleteByReelIdAndUserId(reelId, userId)
        reelsRepository.save(reel.copy(likesCount = maxOf(0,  - 1)))
    }

    fun isReelLikedByUser(reelId: UUID, userId: UUID): Boolean {
        return reelLikeRepository.existsByReelIdAndUserId(reelId, userId)
    }

    fun getReelDetailsById(reelId: UUID): Reel{
        val reel = reelsRepository.findById(reelId).orElseThrow {
            ReelNotFoundException()
        }

        return reel
    }

    companion object {
        private const val TRENDS_FOLDER_NAME = "trends"
    }
}