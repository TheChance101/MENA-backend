package net.thechance.dukan.service

import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
) {
    fun isNameExist(name: String): Boolean = dukanRepository.existsByName(name)
}