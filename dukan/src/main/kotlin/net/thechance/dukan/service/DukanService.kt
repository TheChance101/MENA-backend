package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.entity.DukanColor
import net.thechance.dukan.exception.DukanCreationFailedException
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.mapper.toDukan
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.service.model.DukanCreationParams
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.enums.EnumEntries

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val dukanCategoryRepository: DukanCategoryRepository
) {
    fun getAllStyles(): EnumEntries<Dukan.Style> = Dukan.Style.entries

    fun getAllCategories(): List<DukanCategory> = dukanCategoryRepository.findAll()

    fun getAllColors(): List<DukanColor> = dukanColorRepository.findAll()

    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()

    fun getDukanByOwnerId(ownerId: UUID): Dukan {
        return  dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
    }

    fun createDukan(params: DukanCreationParams): Dukan {
        validateDukanCreation(params)

        val categories = dukanCategoryRepository.findAllById(params.categoryIds)
            .toSet()
            .ifEmpty { throw DukanCreationFailedException() }
        val color = dukanColorRepository.findById(params.colorId)
            .orElseThrow { DukanCreationFailedException() }

        val dukan = params.toDukan(
            color = color,
            categories = categories
        )
        return dukanRepository.save(dukan)
    }
    @Transactional
    fun uploadDukanImage(ownerId: UUID, file: MultipartFile): String {
        val dukan = dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
        val imageUrl = file.originalFilename.orEmpty() // TODO (Upload the image to real storage service)
        dukanRepository.save(dukan.copy(imageUrl = imageUrl))
        return imageUrl
    }

    private fun validateDukanCreation(params: DukanCreationParams) {
        if (dukanRepository.existsByOwnerId(params.ownerId)) {
            throw DukanCreationFailedException()
        }

        if (dukanRepository.existsByName(params.name)) {
            throw DukanCreationFailedException()
        }
    }
}