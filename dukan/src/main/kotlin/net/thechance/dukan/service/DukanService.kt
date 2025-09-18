package net.thechance.dukan.service

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.exception.DukanCreationFailedException
import net.thechance.dukan.mapper.toDukan
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.service.model.DukanCreationParams
import org.springframework.stereotype.Service

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val dukanCategoryRepository: DukanCategoryRepository,
) {
    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()

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

    private fun validateDukanCreation(params: DukanCreationParams) {
        if (dukanRepository.existsByOwnerId(params.ownerId)) {
            throw DukanCreationFailedException()
        }

        if (dukanRepository.existsByName(params.name)) {
            throw DukanCreationFailedException()
        }
    }
}