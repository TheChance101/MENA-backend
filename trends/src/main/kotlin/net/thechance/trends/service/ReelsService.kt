package net.thechance.trends.service

import net.thechance.trends.entity.Reel
import net.thechance.trends.exception.ReelNotFoundException
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.exception.InvalidFileTypeException
import net.thechance.trends.exception.VideoDeleteFailedException
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
    private val fileStorageService: FileStorageService,
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

        val categories = categoryIds.map { categoryRepository.getReferenceById(it) }.toSet()
        if (categories.isEmpty()) throw TrendCategoryNotFoundException()

        val updatedReel = existingReel.copy(
            description = newDescription,
            categories = categories
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

        if (thumbnailFile.contentType?.startsWith("image/") == false) {
            throw InvalidFileTypeException()
        }

        val thumbnailUrl = fileStorageService.uploadThumbnail(
            file = thumbnailFile,
            fileName = thumbnailFile.originalFilename ?: "thumbnail",
            folderName = TRENDS_FOLDER_NAME
        )

        val updatedReel = existingReel.copy(thumbnailUrl = thumbnailUrl)

        return reelsRepository.save(updatedReel)
    }

    companion object {
        private const val TRENDS_FOLDER_NAME = "trends"
    }
}