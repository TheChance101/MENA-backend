package net.thechance.dukan.service

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val dukanCategoryRepository: DukanCategoryRepository
) {
    fun getAllStyles() = Dukan.Style.entries
    fun getAllCategories(): List<DukanCategory> = dukanCategoryRepository.findAll()
    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()
}