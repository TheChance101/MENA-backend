package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.api.mapper.dukan.toDukan
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.entity.DukanColor
import net.thechance.dukan.service.model.DukanWithFavorite
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.service.exception.DukanCreationFailedException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.dukan.service.model.DukanCreationParams
import net.thechance.events.publisher.MenaEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.enums.EnumEntries

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val imageStorageService: ImageStorageService,
    private val dukanCategoryRepository: DukanCategoryRepository,
    private val eventPublisher: MenaEventPublisher
) {
    fun getAllStyles(): EnumEntries<Dukan.Style> = Dukan.Style.entries

    fun getAllCategories(): List<DukanCategory> = dukanCategoryRepository.findAll()

    fun getAllColors(): List<DukanColor> = dukanColorRepository.findAll()

    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()

    fun getDukanByOwnerId(ownerId: UUID): Dukan {
        return dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
    }

    fun createDukan(params: DukanCreationParams): Dukan {
        try {
            validateDukanCreation(params)

            val categories = params.categoryIds.map { id -> dukanCategoryRepository.getReferenceById(id) }
                .toSet()
                .ifEmpty { throw DukanCreationFailedException() }

            val color = dukanColorRepository.getReferenceById(params.colorId)

            val dukan = params.toDukan(
                color = color,
                categories = categories
            )
            return dukanRepository.save(dukan)
        } catch (_: EntityNotFoundException) {
            throw DukanCreationFailedException()
        }
    }

    @Transactional
    fun uploadDukanImage(ownerId: UUID, file: MultipartFile): String {
        val dukan = dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
        val imageUrl =
            imageStorageService.uploadImage(
                file = file,
                fileName = "${dukan.name}-${file.originalFilename}",
                folderName = DUKAN_FOLDER_NAME
            )
        dukanRepository.save(dukan.copy(imageUrl = imageUrl))
        return imageUrl
    }

    fun getDukanDetailsById(dukanId: UUID): Dukan {
        return dukanRepository.findByIdOrNull(dukanId) ?: throw DukanNotFoundException()
    }

    fun getAllByCategoryId(categoryId: UUID, pageable: Pageable): Page<Dukan> {
        return dukanRepository.findApprovedDukansWithProductsByCategory(categoryId, pageable)
    }

    fun getAllEditorPicksDukan(userId: UUID, pageable: Pageable): Page<DukanWithFavorite> {
        // TODO: Filter by user preferences once data model is ready
        return dukanRepository.findAllApprovedWithShelvesAndProducts(userId, pageable)
    }

    private fun validateDukanCreation(params: DukanCreationParams) {
        if (dukanRepository.existsByOwnerId(params.ownerId)) {
            throw DukanCreationFailedException()
        }

        if (dukanRepository.existsByName(params.name)) {
            throw DukanCreationFailedException()
        }
    }

    fun getAllBestDukansAround(
        lat: Double,
        lng: Double,
        pageable: Pageable,
        range: Double = 30000.0
    ): Page<Dukan> {
        return dukanRepository.findBestAroundApprovedDukans(lat, lng, range, pageable)
    }

    fun getAllByCategory(categoryId: UUID, userId: UUID, pageable: Pageable): Page<DukanWithFavorite> =
        dukanRepository.findAllByCategoryWithFavorite(categoryId, userId, pageable)

    companion object {
        private val DUKAN_FOLDER_NAME = "dukan"
    }
}