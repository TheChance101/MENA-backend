package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.api.mapper.dukan.toDukan
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.entity.DukanColor
import net.thechance.dukan.entity.StatusChangelog
import net.thechance.dukan.repository.*
import net.thechance.dukan.service.exception.DukanCreationFailedException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.dukan.service.exception.DukanUserNotFoundException
import net.thechance.dukan.service.mapper.toDukanCreationEvent
import net.thechance.dukan.service.mapper.toDukanUpdateEvent
import net.thechance.dukan.service.model.DukanCreationParams
import net.thechance.dukan.service.model.DukanWithDiscount
import net.thechance.dukan.service.model.DukanWithFavorite
import net.thechance.events.publisher.MenaEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*
import kotlin.enums.EnumEntries

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val imageStorageService: ImageStorageService,
    private val dukanCategoryRepository: DukanCategoryRepository,
    private val statusChangeLogRepository: StatusChangelogRepository,
    private val userRepository: DukanUserRepository,
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
            val user = userRepository.findById(params.ownerId).orElseThrow { DukanUserNotFoundException() }

            validateDukanCreation(params)

            val categories = params.categoryIds.map { id -> dukanCategoryRepository.getReferenceById(id) }
                .toSet()
                .ifEmpty { throw DukanCreationFailedException() }

            val color = dukanColorRepository.getReferenceById(params.colorId)

            val dukan = params.toDukan(
                color = color,
                categories = categories
            )

            val updatedUser = user.copy(dukan = dukan, updatedAt = Instant.now())
            userRepository.save(updatedUser)

            val savedDukan = dukanRepository.save(dukan)

            eventPublisher.publish(savedDukan.toDukanCreationEvent())

            return savedDukan
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

        val savedDukan = dukanRepository.save(dukan.copy(imageUrl = imageUrl))

        eventPublisher.publish(savedDukan.toDukanUpdateEvent())

        return imageUrl
    }

    fun getDukanDetailsById(dukanId: UUID): Dukan {
        return dukanRepository.findByIdOrNull(dukanId) ?: throw DukanNotFoundException()
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

    fun getAllByCategoryIdWithFavorite(categoryId: UUID, userId: UUID, pageable: Pageable): Page<DukanWithFavorite> =
        dukanRepository.findApprovedDukansWithProductsByCategoryWithFavorite(categoryId, userId, pageable)

    fun getDukansByStatusAndQuery(
        query: String,
        status: Dukan.Status,
        pageable: Pageable
    ): Page<Dukan> =
        dukanRepository.findByNameOrAddressAndStatus(query = query, status = status, pageable = pageable)

    @Transactional
    fun updateDukanStatus(dukanId: UUID, status: Dukan.Status, reason: String?) {
        val isUpdated = dukanRepository.updateStatus(dukanId, status) > 0
        if (!isUpdated) throw DukanNotFoundException()

        handleUpdatingStatusActions(dukanId, status, reason)
    }

    fun findTopDukansWithDiscounts(
        userId: UUID,
        pageable: Pageable
    ): Page<DukanWithDiscount> {
        return dukanRepository.findTopDukansWithDiscounts(userId, pageable)
    }

    private fun handleUpdatingStatusActions(dukanId: UUID, status: Dukan.Status, reason: String?) {
        if (status == Dukan.Status.REJECTED) {
            insertRejectionChangelog(dukanId, reason)
        }
        if (status == Dukan.Status.APPROVED) {
            dukanRepository.updateActivationStatus(
                dukanId = dukanId,
                activationStatus = Dukan.ActivationStatus.ACTIVATED,
            )

        }

        val dukan = getDukanDetailsById(dukanId)
        eventPublisher.publish(dukan.toDukanUpdateEvent())
    }

    @Transactional
    fun updateDukanActivationStatus(
        dukanId: UUID,
        reason: String?,
        activationStatus: Dukan.ActivationStatus
    ) {
        val activationStatusUpdated = dukanRepository.updateActivationStatus(
            dukanId = dukanId,
            activationStatus = activationStatus,
        ) > 0
        if (!activationStatusUpdated) throw DukanNotFoundException()
        if (activationStatus == Dukan.ActivationStatus.DEACTIVATED) {
            insertDeactivationChangelog(
                dukanId = dukanId,
                reason = reason
            )
        }
        val dukan = getDukanDetailsById(dukanId)
        eventPublisher.publish(dukan.toDukanUpdateEvent())
    }

    private fun insertDeactivationChangelog(dukanId: UUID, reason: String?) {
        val changelog = StatusChangelog(
            dukanId = dukanId,
            status = StatusChangelog.Status.DEACTIVATED,
            reason = reason.orEmpty()
        )
        statusChangeLogRepository.save(changelog)
    }

    private fun insertRejectionChangelog(dukanId: UUID, reason: String?) {
        val changelog = StatusChangelog(
            dukanId = dukanId,
            status = StatusChangelog.Status.REJECTED,
            reason = reason.orEmpty()
        )
        statusChangeLogRepository.save(changelog)
    }

    companion object {
        private val DUKAN_FOLDER_NAME = "dukan"
    }
}