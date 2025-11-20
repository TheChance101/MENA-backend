package net.thechance.trends.service

import net.thechance.trends.entity.Trend
import net.thechance.trends.entity.TrendLike
import net.thechance.trends.entity.TrendView
import net.thechance.trends.entity.UserCategories
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.exception.TrendNotFoundException
import net.thechance.trends.models.TrendSignedUrls
import net.thechance.trends.models.TrendWithLikeStatus
import net.thechance.trends.models.TrendWithOwnerShipAndLikeStatus
import net.thechance.trends.models.withOwnership
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.TrendLikeRepository
import net.thechance.trends.repository.TrendViewRepository
import net.thechance.trends.repository.TrendsRepository
import net.thechance.trends.repository.UserCategoryRepository
import net.thechance.trends.service.config.TrendsExpirationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.random.Random


@Service
@EnableConfigurationProperties(TrendsExpirationProperties::class)
class TrendsService(
    private val trendsRepository: TrendsRepository,
    private val categoryRepository: CategoryRepository,
    private val fileStorageService: FileStorageService,
    private val trendViewRepository: TrendViewRepository,
    private val trendLikeRepository: TrendLikeRepository,
    private val trendsExpirationProperties: TrendsExpirationProperties,
    private val userCategoryRepository: UserCategoryRepository
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
        ).map {
            generatePresignedUrlsForTrend(it).withOwnership(currentUserId)
        }
        return body
    }

    fun getAllTrendsForFeed(
        pageable: Pageable,
        currentUserId: UUID,
        trendId: UUID? = null,
    ): Page<TrendWithOwnerShipAndLikeStatus> {

        val sortOrder = pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))

        val userCategories = userCategoryRepository.findAllByUserIdAndIsSelectedOrderByAffinityDesc(currentUserId, true)

        if (userCategories.isEmpty()) {
            return Page.empty(pageable)
        }

        val buckets = userCategories.bucketByValue { it.affinity }

        val topCategories = selectTopBucketCategories(buckets, pageable.pageNumber)
        val randomCategories = selectRandomBucketCategories(buckets, pageable.pageNumber)

        val topTrendsPageable = PageRequest.of(pageable.pageNumber, 7, sortOrder)
        val topTrends = trendsRepository.getTrendFeedForCategories(
            currentUserId,
            topTrendsPageable,
            emptyList(),
            topCategories
        ).content

        val topTrendIds = topTrends.map { it.getTrend().id }

        val bottomTrendsPageable = if(trendId != null) {
            PageRequest.of(pageable.pageNumber, 2,sortOrder)
        } else PageRequest.of(pageable.pageNumber, 3, sortOrder)

        val bottomTrends = trendsRepository.getTrendFeedForCategories(
            currentUserId,
            bottomTrendsPageable,
            topTrendIds,
            randomCategories
        ).content

        val finalTrends = if(trendId != null) {
            val list = mutableListOf<TrendWithLikeStatus>()
            val trend = trendsRepository.findByIdAndIsPublishedWithLikeStatus(trendId, currentUserId, true)
            trend?.let { list.add(it) }
            (list + (topTrends + bottomTrends).shuffled())
                .map { generatePresignedUrlsForTrend(it).withOwnership(currentUserId) }
        } else {
            (topTrends + bottomTrends).shuffled()
                .map { generatePresignedUrlsForTrend(it).withOwnership(currentUserId) }
        }


        return PageImpl(finalTrends, pageable, finalTrends.size.toLong())
    }

    private fun selectTopBucketCategories(
        buckets: List<List<UserCategories>>,
        pageNumber: Int
    ): List<UUID> {
        if (buckets.isEmpty()) return emptyList()

        val random = Random(pageNumber)

        if (buckets.size == 1) {
            return buckets[0].map { it.categoryId }
        }

        val selected = mutableListOf<UUID>()
        val probabilityDecay = 1.0 / buckets.size

        buckets.forEachIndexed { bucketIndex, bucket ->
            val probability = 1.0 - (bucketIndex * probabilityDecay)

            bucket.forEach { category ->
                if (random.nextDouble() < probability) {
                        selected.add(category.categoryId)
                }
            }
        }

        if (selected.isEmpty()) {
            selected.addAll(buckets.first().map { it.categoryId })
        }

        return selected
    }

    private fun selectRandomBucketCategories(
        buckets: List<List<UserCategories>>,
        pageNumber: Int
    ): List<UUID> {
        if (buckets.isEmpty()) return emptyList()

        val random = Random(pageNumber * 31)
        val allCategories = buckets.flatten()

        val count = minOf((2..4).random(random), allCategories.size)
        return allCategories
            .shuffled(random)
            .take(count)
            .map { it.categoryId }
    }

    private fun <T> List<T>.bucketByValue(
        range: Int = 10,
        valueSelector: (T) -> Int
    ): List<List<T>> =
        this.fold(emptyList()) { groups, item ->
            if (groups.isEmpty()) {
                listOf(listOf(item))
            } else {
                val lastGroup = groups.last()
                val pivotValue = valueSelector(lastGroup.first())

                if (valueSelector(item) in (pivotValue - range)..(pivotValue + range)) {
                    groups.dropLast(1) + listOf(lastGroup + item)
                } else {
                    groups + listOf(listOf(item))
                }
            }
        }

    fun getUserFavoriteTrends(
        pageable: Pageable,
        currentUserId: UUID,
        trendId: UUID? = null,
    ): Page<TrendWithOwnerShipAndLikeStatus> {
        val adjustedPageable = PageRequest.of(
            pageable.pageNumber,
            10,
            pageable.getSortOr(Sort.by(Sort.Direction.DESC, "createdAt"))
        )

        return trendsRepository
            .getUserLikedTrends(currentUserId, trendId, adjustedPageable)
            .map {
                generatePresignedUrlsForTrend(it)
                    .withOwnership(currentUserId)
            }
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
        return trendsRepository.findByIdAndIsPublishedWithLikeStatus(
            trendId = trendId,
            isPublished = true,
            userId = userId
        ) ?: throw TrendNotFoundException()
    }

    private fun generatePresignedUrlsForTrend(
        trendWithLikeStatus: TrendWithLikeStatus
    ): TrendWithLikeStatus {
        runCatching {
            val trend = trendWithLikeStatus.getTrend()
            val signedUrls = generatePresignedUrlsForTrend(
                videoKey = trend.videoUrl, thumbnailKey = trend.thumbnailUrl
            )

            val updatedTrend = trend.copy(
                videoUrl = signedUrls.videoUrl, thumbnailUrl = signedUrls.thumbnailUrl
            )

            return createTrendWithLikeStatus(updatedTrend, trendWithLikeStatus.getIsLiked())
        }.getOrElse {
            return trendWithLikeStatus
        }
    }

    private fun createTrendWithLikeStatus(trend: Trend, isLiked: Boolean): TrendWithLikeStatus {
        return object : TrendWithLikeStatus {
            override fun getTrend(): Trend = trend
            override fun getIsLiked(): Boolean = isLiked
        }
    }

    private fun generatePresignedUrlsForTrend(videoKey: String, thumbnailKey: String?): TrendSignedUrls {
        val signedVideoUrl =
            fileStorageService.generatePresignedUrl(videoKey, trendsExpirationProperties.videoUrlMinutes)
        val signedThumbnailUrl = thumbnailKey?.let {
            fileStorageService.generatePresignedUrl(it, trendsExpirationProperties.thumbnailUrlMinutes)
        }
        return TrendSignedUrls(videoUrl = signedVideoUrl, thumbnailUrl = signedThumbnailUrl)
    }

    fun generatePresignedUrlsForTrend(trendId: UUID): TrendSignedUrls {
        val trendUrls = trendsRepository.findTrendUrlsById(trendId) ?: throw TrendNotFoundException()
        val signedVideoUrl = fileStorageService.generatePresignedUrl(
            trendUrls.getTrendVideoUrl(), trendsExpirationProperties.videoUrlMinutes
        )
        val signedThumbnailUrl = trendUrls.getTrendThumbnailUrl()?.let {
            fileStorageService.generatePresignedUrl(it, trendsExpirationProperties.thumbnailUrlMinutes)
        }
        return TrendSignedUrls(videoUrl = signedVideoUrl, thumbnailUrl = signedThumbnailUrl)
    }
}