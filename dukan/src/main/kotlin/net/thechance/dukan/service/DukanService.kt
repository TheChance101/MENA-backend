package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.api.dto.DukanCreationRequest
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.exception.DukanCreationFailedException
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val dukanCategoryRepository: DukanCategoryRepository,
) {
    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()

    fun createDukan(
        dukanRequest: DukanCreationRequest,
        ownerId: UUID,
    ): Dukan {
        validateDukanCreation(dukanRequest, ownerId)

        val dukan = dukanRequest.toDukan(ownerId = ownerId)
        return dukanRepository.save(dukan)
    }

    fun DukanCreationRequest.toDukan(
        ownerId: UUID
    ): Dukan {
        val categories = dukanCategoryRepository.findAllById(categoryIds)
            .toSet()
            .ifEmpty { throw DukanCreationFailedException() }
        val color = dukanColorRepository.findById(colorId)
            .orElseThrow { DukanCreationFailedException() }
        return Dukan(
            name = name,
            categories = categories,
            address = address,
            latitude = latitude,
            longitude = longitude,
            color = color,
            style = style,
            ownerId = ownerId,
        )
    }

    @Transactional
    fun uploadDukanImage(ownerId: UUID, file: MultipartFile): String {
        val dukan = dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
        val imageUrl = file.originalFilename.orEmpty() // TODO (Upload the image to real storage service)
        dukanRepository.save(dukan.copy(imageUrl = imageUrl))
        return imageUrl
    }

    private fun validateDukanCreation(request: DukanCreationRequest, ownerId: UUID) {
        if (dukanRepository.existsByOwnerId(ownerId)) {
            throw DukanCreationFailedException()
        }

        if (dukanRepository.existsByName(request.name)) {
            throw DukanCreationFailedException()
        }
    }
}