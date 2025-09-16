package net.thechance.dukan.service

import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
) {
    fun isDukanNameAvailable(name: String): Boolean {
        return dukanRepository.existsByName(name).not()
    }
}