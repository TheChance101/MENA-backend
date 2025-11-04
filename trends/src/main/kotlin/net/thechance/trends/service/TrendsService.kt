package net.thechance.trends.service

import net.thechance.trends.entity.Trend
import net.thechance.trends.entity.TrendLike
import net.thechance.trends.entity.TrendView
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.exception.TrendNotFoundException
import net.thechance.trends.models.TrendWithLikeStatus
import net.thechance.trends.models.TrendWithOwnerShipAndLikeStatus
import net.thechance.trends.models.withOwnership
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.TrendLikeRepository
import net.thechance.trends.repository.TrendViewRepository
import net.thechance.trends.repository.TrendsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class TrendsService(
    private val trendsRepository: TrendsRepository,
    private val categoryRepository: CategoryRepository,
    private val fileStorageService: FileStorageService,
    private val trendViewRepository: TrendViewRepository,
    private val trendLikeRepository: TrendLikeRepository
) {
    fun getAllTrendsByUserId(
        pageable: Pageable,
        currentUserId: UUID,
        trendId: UUID?
    ): Page<TrendWithOwnerShipAndLikeStatus> {

        val body = trendsRepository.findByOwnerIdAndIsPublished(
            currentUserId,
            true,
            trendId,
            PageRequest.of(
                pageable.pageNumber,
                10,
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
            )
        ).map { it.withOwnership(currentUserId = currentUserId) }
        return body
    }

    fun getAllTrendsForFeed(
        pageable: Pageable,
        currentUserId: UUID,
        trendId: UUID? = null,
    ): Page<TrendWithOwnerShipAndLikeStatus> {
        val adjustedPageable = PageRequest.of(
            pageable.pageNumber,
            10,
            pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
        )

        val trends = trendsRepository.getTrendFeedForUser(currentUserId, trendId, adjustedPageable).map { it.withOwnership(currentUserId = currentUserId) }

        return trends
    }

    @Transactional
    fun deleteTrendById(id: UUID, currentUserId: UUID) {
        val trendUrls = trendsRepository.findVideoUrlByIdAndOwnerId(id, currentUserId)
            ?: throw TrendNotFoundException()

        trendsRepository.deleteTrendById(id)
        fileStorageService.deleteFile(trendUrls.getTrendVideoUrl())
        trendUrls.getTrendThumbnailUrl()?.let { fileStorageService.deleteFile(it) }
    }

    @Transactional
    fun updateTrendDescriptionAndCategories(
        trendId: UUID,
        ownerId: UUID,
        newDescription: String,
        categoryIds: Set<UUID>
    ): Trend {
        val existingTrend = trendsRepository.findByIdAndOwnerId(id = trendId, ownerId = ownerId)
            ?: throw TrendNotFoundException()

        val categories = categoryIds.map { categoryRepository.getReferenceById(it) }.toMutableSet()
        if (categories.isEmpty()) throw TrendCategoryNotFoundException()

        val updatedTrend = existingTrend.getTrend().copy(
            description = newDescription,
            categories = categories,
            isPublished = true
        )

        return trendsRepository.save(updatedTrend)
    }


    fun uploadTrend(currentUserId: UUID, file: MultipartFile): UUID {
        val videoUrl = fileStorageService.uploadVideo(file = file)
        val trend = Trend(
            ownerId = currentUserId,
            videoUrl = videoUrl
        )
        return trendsRepository.save(trend).id
    }

    @Transactional
    fun uploadThumbnail(
        trendId: UUID,
        ownerId: UUID,
        thumbnailFile: MultipartFile
    ): Trend {
        val existingTrend = trendsRepository.findByIdAndOwnerId(id = trendId, ownerId = ownerId)
            ?: throw TrendNotFoundException()

        val thumbnailUrl = fileStorageService.uploadImage(file = thumbnailFile)

        val updatedTrend = existingTrend.getTrend().copy(thumbnailUrl = thumbnailUrl)

        return trendsRepository.save(updatedTrend)
    }

    @Transactional
    fun incrementViewCount(trendId: UUID, userId: UUID) {
        trendViewRepository.save(TrendView(trendId = trendId, userId = userId))
    }

    fun likeTrend(trendId: UUID, userId: UUID): TrendWithOwnerShipAndLikeStatus {
        trendLikeRepository.save(TrendLike(trendId = trendId, userId = userId))
        return getTrendOrThrow(trendId, userId).withOwnership(currentUserId = userId)
    }

    @Transactional
    fun unlikeTrend(trendId: UUID, userId: UUID): TrendWithOwnerShipAndLikeStatus {
        trendLikeRepository.deleteTrendLikeByTrendIdAndUserId(trendId, userId)
        return getTrendOrThrow(trendId, userId).withOwnership(currentUserId = userId)
    }

    fun getTrendOrThrow(trendId: UUID, userId: UUID): TrendWithLikeStatus {
        return trendsRepository.findByIdAndIsPublishedWithLikeStatus(trendId = trendId, isPublished = true, userId = userId) ?: throw TrendNotFoundException()
    }
}