package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanColor
import net.thechance.dukan.api.dto.DukanStyleResponse
import net.thechance.dukan.mapper.toDukanStyleResponse
import kotlin.enums.EnumEntries
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val dukanCategoryRepository: DukanCategoryRepository
) {
    fun getAllStyles(): EnumEntries<Dukan.Style> = Dukan.Style.entries
    fun getAllCategories(): List<DukanCategory> = dukanCategoryRepository.findAll()
    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()
    fun getAllColors(): List<DukanColor> = dukanColorRepository.findAll()
    fun getDukanByOwnerId(ownerId: UUID): Dukan {
       return  dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
    }
}