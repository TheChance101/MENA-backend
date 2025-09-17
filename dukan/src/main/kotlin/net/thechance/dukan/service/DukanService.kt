package net.thechance.dukan.service

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
) {
    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()
    fun getDukanStatues(ownerId: UUID): Dukan.Status {
        val dukan = dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
        return dukan.status
    }
}